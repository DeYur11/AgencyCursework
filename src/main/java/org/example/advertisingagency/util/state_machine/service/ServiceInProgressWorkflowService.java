package org.example.advertisingagency.util.state_machine.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.enums.ServiceEvent;
import org.example.advertisingagency.enums.ServiceStatusType;
import org.example.advertisingagency.model.ServiceInProgressStatus;
import org.example.advertisingagency.model.ServicesInProgress;
import org.example.advertisingagency.repository.ServiceInProgressStatusRepository;
import org.example.advertisingagency.repository.ServicesInProgressRepository;
import org.example.advertisingagency.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceInProgressWorkflowService {

    private final StateMachine<ServiceStatusType, ServiceEvent> stateMachine;
    private final ServicesInProgressRepository sipRepository;
    private final ServiceInProgressStatusRepository serviceStatusRepository;
    private final ProjectWorkflowService projectWorkflowService;
    private final TaskRepository taskRepository;

    public ServiceInProgressWorkflowService(
            @Qualifier("serviceInProgressStateMachine") StateMachine<ServiceStatusType, ServiceEvent> stateMachine,
            ServicesInProgressRepository sipRepository,
            ServiceInProgressStatusRepository serviceStatusRepository,
            ProjectWorkflowService projectWorkflowService,
            TaskRepository taskRepository) {
        this.stateMachine = stateMachine;
        this.sipRepository = sipRepository;
        this.serviceStatusRepository = serviceStatusRepository;
        this.projectWorkflowService = projectWorkflowService;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public ServicesInProgress transition(Integer sipId, ServiceEvent event) {
        ServicesInProgress sip = sipRepository.findById(sipId)
                .orElseThrow(() -> new EntityNotFoundException("ServiceInProgress not found"));

        ServiceStatusType currentStatus = ServiceStatusType.from(sip.getStatus().getName());

        stateMachine.stop();
        stateMachine.getStateMachineAccessor().doWithAllRegions(region ->
                region.resetStateMachine(new DefaultStateMachineContext<>(currentStatus, null, null, null))
        );
        stateMachine.start();

        boolean accepted = stateMachine.sendEvent(event);
        if (!accepted) {
            throw new IllegalStateException("Invalid transition for: " + currentStatus + " → " + event);
        }

        ServiceStatusType newStatus = stateMachine.getState().getId();
        String dbName = ServiceStatusType.toDb(newStatus);

        ServiceInProgressStatus statusEntity = serviceStatusRepository.findByName(dbName)
                .orElseThrow(() -> new EntityNotFoundException("ServiceStatus not found: " + dbName));

        sip.setStatus(statusEntity);
        ServicesInProgress updated = sipRepository.save(sip);

        projectWorkflowService.updateProjectStatusIfNeeded(sip.getProjectService().getProject().getId());

        return updated;
    }

    @Transactional
    public void updateServiceStatusIfNeeded(Integer serviceId) {
        ServicesInProgress sip = sipRepository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("ServiceInProgress not found"));

        var tasks = taskRepository.findAllByServiceInProgress_Id(serviceId);
        if (tasks == null || tasks.isEmpty()) return;

        boolean allCompleted = tasks.stream()
                .allMatch(task -> task.getTaskStatus().getName().equalsIgnoreCase("Completed"));

        boolean allNotStarted = tasks.stream()
                .allMatch(task -> task.getTaskStatus().getName().equalsIgnoreCase("Not Started"));

        boolean anyActive = tasks.stream()
                .anyMatch(task -> {
                    String name = task.getTaskStatus().getName().toLowerCase();
                    return !name.equals("not started") && !name.equals("completed");
                });

        boolean hasAnyNotCompleted = tasks.stream()
                .anyMatch(task -> !task.getTaskStatus().getName().equalsIgnoreCase("Completed"));

        ServiceStatusType current = ServiceStatusType.from(sip.getStatus().getName());

        if (allCompleted && current != ServiceStatusType.COMPLETED) {
            transition(serviceId, ServiceEvent.COMPLETE);

        } else if (!allNotStarted && anyActive && current == ServiceStatusType.NOT_STARTED) {
            transition(serviceId, ServiceEvent.START);

        } else if (current == ServiceStatusType.COMPLETED && hasAnyNotCompleted) {
            // ⬅️ основна перевірка: якщо додали нове завдання або оновили статус назад
            transition(serviceId, ServiceEvent.REOPEN);
        }
    }
}
