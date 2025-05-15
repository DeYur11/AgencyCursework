package org.example.advertisingagency.util.state_machine.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.enums.TaskEvent;
import org.example.advertisingagency.enums.TaskStatusType;
import org.example.advertisingagency.enums.ServiceStatusType;
import org.example.advertisingagency.exception.InvalidMaterialState;
import org.example.advertisingagency.model.*;
import org.example.advertisingagency.repository.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.access.StateMachineAccessor;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class TaskWorkflowService {

    private final StateMachine<TaskStatusType, TaskEvent> stateMachine;
    private final TaskRepository taskRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final MaterialRepository materialRepository;
    private final ServicesInProgressRepository sipRepository;
    private final ServiceInProgressStatusRepository serviceStatusRepository;
    private final ProjectWorkflowService projectWorkflowService;

    public TaskWorkflowService(
            @Qualifier("taskStateMachine") StateMachine<TaskStatusType, TaskEvent> stateMachine,
            TaskRepository taskRepository,
            TaskStatusRepository taskStatusRepository,
            MaterialRepository materialRepository,
            ServicesInProgressRepository sipRepository,
            ServiceInProgressStatusRepository serviceStatusRepository,
            ProjectWorkflowService projectWorkflowService) {
        this.stateMachine = stateMachine;
        this.taskRepository = taskRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.materialRepository = materialRepository;
        this.sipRepository = sipRepository;
        this.serviceStatusRepository = serviceStatusRepository;
        this.projectWorkflowService = projectWorkflowService;
    }

    @Transactional
    public Task transition(Integer taskId, TaskEvent event) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + taskId));

        TaskStatusType currentStatus = TaskStatusType.from(task.getTaskStatus().getName());

        if (event == TaskEvent.COMPLETE && !allMaterialsAccepted(taskId)) {
            throw new InvalidMaterialState("Неможливо завершити завдання, деякі завдання ще не прийняті");
        }

        prepareStateMachine(currentStatus);

        boolean accepted = stateMachine.sendEvent(MessageBuilder.withPayload(event).build());
        if (!accepted) {
            throw new IllegalStateException("Неприпустима зміна статусу: " + currentStatus.name() + " -> " + event);
        }

        TaskStatusType newStatus = stateMachine.getState().getId();

        switch (newStatus) {
            case COMPLETED: task.setEndDate(Instant.now());
            case IN_PROGRESS: task.setStartDate(Instant.now());
        }

        String newStatusName = TaskStatusType.toDb(newStatus);

        TaskStatus statusEntity = taskStatusRepository.findByName(newStatusName)
                .orElseThrow(() -> new EntityNotFoundException("TaskStatus not found: " + newStatusName));

        task.setTaskStatus(statusEntity);
        Task updated = taskRepository.save(task);

        updateServiceStatusIfNeeded(task.getServiceInProgress().getId());

        return updated;
    }

    private boolean allMaterialsAccepted(Integer taskId) {
        return materialRepository.findAllByTask_Id(taskId).stream()
                .allMatch(m -> m.getStatus().getName().equalsIgnoreCase("Accepted"));
    }

    private void prepareStateMachine(TaskStatusType currentState) {
        stateMachine.stop();
        StateMachineAccessor<TaskStatusType, TaskEvent> accessor = stateMachine.getStateMachineAccessor();
        accessor.doWithAllRegions(region -> region.resetStateMachine(new DefaultStateMachineContext<>(currentState, null, null, null)));
        stateMachine.start();
    }

    private void updateServiceStatusIfNeeded(Integer serviceId) {
        List<Task> tasks = taskRepository.findAllByServiceInProgress_Id(serviceId);
        if (tasks.isEmpty()) return;

        boolean allCompleted = tasks.stream()
                .allMatch(t -> t.getTaskStatus().getName().equalsIgnoreCase("Completed"));
        boolean allNotStarted = tasks.stream()
                .allMatch(t -> t.getTaskStatus().getName().equalsIgnoreCase("Not Started"));
        boolean anyInProgressOrOnHold = tasks.stream()
                .anyMatch(t -> {
                    String s = t.getTaskStatus().getName().toLowerCase();
                    return s.equals("in progress") || s.equals("on hold");
                });

        ServiceStatusType newStatus;
        if (allCompleted) {
            newStatus = ServiceStatusType.COMPLETED;
        } else if (allNotStarted) {
            newStatus = ServiceStatusType.NOT_STARTED;
        } else if (anyInProgressOrOnHold) {
            newStatus = ServiceStatusType.IN_PROGRESS;
        } else {
            return; // No change
        }

        ServicesInProgress sip = sipRepository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("ServicesInProgress not found with id: " + serviceId));

        String dbStatus = ServiceStatusType.toDb(newStatus);

        if (!sip.getStatus().getName().equalsIgnoreCase(dbStatus)) {
            ServiceInProgressStatus statusEntity = serviceStatusRepository.findByName(dbStatus)
                    .orElseThrow(() -> new EntityNotFoundException("ServiceStatus not found: " + dbStatus));
            sip.setStatus(statusEntity);
            sipRepository.save(sip);
        }
        projectWorkflowService.updateProjectStatusIfNeeded(sip.getProjectService().getProject().getId());
    }
}
