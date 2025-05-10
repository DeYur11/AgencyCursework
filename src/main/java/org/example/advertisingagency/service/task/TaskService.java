package org.example.advertisingagency.service.task;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.task.CreateTaskInput;
import org.example.advertisingagency.dto.task.PaginatedTasksInput;
import org.example.advertisingagency.dto.task.UpdateTaskInput;
import org.example.advertisingagency.enums.TaskEvent;
import org.example.advertisingagency.event.AuditLogEvent;
import org.example.advertisingagency.exception.InvalidStateTransitionException;
import org.example.advertisingagency.listener.AuditLogEventListener;
import org.example.advertisingagency.model.*;
import org.example.advertisingagency.model.log.AuditAction;
import org.example.advertisingagency.model.log.AuditEntity;
import org.example.advertisingagency.model.log.AuditLog;
import org.example.advertisingagency.repository.*;
import org.example.advertisingagency.service.auth.UserContextHolder;
import org.example.advertisingagency.service.logs.AuditLogPublisher;
import org.example.advertisingagency.specification.TaskSpecifications;
import org.example.advertisingagency.util.state_machine.service.ServiceInProgressWorkflowService;
import org.example.advertisingagency.util.state_machine.service.TaskWorkflowService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ServicesInProgressRepository servicesInProgressRepository;
    private final WorkerRepository workerRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final MaterialKeywordRepository materialKeywordRepository;
    private final TaskWorkflowService taskWorkflowService;
    private final ServiceInProgressWorkflowService serviceInProgressWorkflowService;
    private final AuditLogEventListener auditLogEventListener;
    private final AuditLogPublisher auditLogPublisher;
    private final ApplicationEventPublisher eventPublisher;

    public TaskService(TaskRepository taskRepository,
                       ServicesInProgressRepository servicesInProgressRepository,
                       WorkerRepository workerRepository,
                       TaskStatusRepository taskStatusRepository,
                       MaterialKeywordRepository materialKeywordRepository,
                       TaskWorkflowService taskWorkflowService, ServiceInProgressWorkflowService serviceInProgressWorkflowService, AuditLogEventListener auditLogEventListener, AuditLogPublisher auditLogPublisher, ApplicationEventPublisher applicationEventPublisher) {
        this.taskRepository = taskRepository;
        this.servicesInProgressRepository = servicesInProgressRepository;
        this.workerRepository = workerRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.materialKeywordRepository = materialKeywordRepository;
        this.taskWorkflowService = taskWorkflowService;
        this.serviceInProgressWorkflowService = serviceInProgressWorkflowService;
        this.auditLogEventListener = auditLogEventListener;
        this.auditLogPublisher = auditLogPublisher;
        this.eventPublisher = applicationEventPublisher;
    }

    public Task getTaskById(Integer id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task createTask(CreateTaskInput input) {
        Task task = new Task();
        task.setName(input.getName());
        task.setDescription(input.getDescription());
        task.setStartDate(input.getStartDate());
        task.setEndDate(input.getEndDate());
        task.setDeadline(input.getDeadline());

        if (input.getServiceInProgressId() != null) {
            ServicesInProgress service = findServiceInProgress(input.getServiceInProgressId());
            task.setServiceInProgress(service);
        }

        if (input.getAssignedWorkerId() != null) {
            task.setAssignedWorker(findWorker(input.getAssignedWorkerId()));
        }

        task.setTaskStatus(findTaskStatus(1)); // Not Started
        task.setPriority(input.getPriority());
        task.setValue(input.getValue());

        Task saved = taskRepository.save(task);

        if (saved.getServiceInProgress() != null) {
            serviceInProgressWorkflowService.updateServiceStatusIfNeeded(saved.getServiceInProgress().getId());
        }

        logTaskAction(AuditAction.CREATE, saved);
        return saved;
    }

    public Task updateTask(Integer id, UpdateTaskInput input) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));

        if (input.getName() != null) task.setName(input.getName());
        if (input.getDescription() != null) task.setDescription(input.getDescription());
        if (input.getStartDate() != null) task.setStartDate(input.getStartDate());
        if (input.getEndDate() != null) task.setEndDate(input.getEndDate());
        if (input.getDeadline() != null) task.setDeadline(input.getDeadline());
        if (input.getServiceInProgressId() != null) task.setServiceInProgress(findServiceInProgress(input.getServiceInProgressId()));
        if (input.getAssignedWorkerId() != null) task.setAssignedWorker(findWorker(input.getAssignedWorkerId()));
        if (input.getPriority() != null) task.setPriority(input.getPriority());
        if (input.getValue() != null) task.setValue(input.getValue());


        if (input.getTaskStatusId() != null) {
            int currentId = task.getTaskStatus() != null ? task.getTaskStatus().getId() : -1;
            if (input.getTaskStatusId() != currentId) {
                TaskStatus newStatus = findTaskStatus(input.getTaskStatusId());
                TaskEvent event = determineEvent(task.getTaskStatus(), newStatus);
                if (event != null) {
                    return taskWorkflowService.transition(id, event); // через state machine
                } else {
                    throw new InvalidStateTransitionException("Invalid status transition: "
                            + currentId + " → " + input.getTaskStatusId());
                }
            }
        }

        Task updated = taskRepository.save(task);
        logTaskAction(AuditAction.UPDATE, updated);
        return updated;
    }

    private TaskEvent determineEvent(TaskStatus current, TaskStatus target) {
        String from = current != null ? current.getName().toLowerCase() : "none";
        String to = target.getName().toLowerCase();

        return switch (from + "->" + to) {
            case "not started->in progress" -> TaskEvent.START;
            case "in progress->on hold"     -> TaskEvent.HOLD;
            case "on hold->in progress", "completed->in progress" -> TaskEvent.RESUME;
            case "in progress->completed"   -> TaskEvent.COMPLETE;
            default -> null;
        };
    }


    public boolean deleteTask(Integer id) {
        if (!taskRepository.existsById(id)) {
            return false;
        }
        Integer serviceInpProgressId = taskRepository.findById(id).get().getServiceInProgress().getId();
        Task deletedTask = taskRepository.findById(id).orElseThrow();
        logTaskAction(AuditAction.DELETE, deletedTask);
        taskRepository.deleteById(id);
        serviceInProgressWorkflowService.updateServiceStatusIfNeeded(serviceInpProgressId);
        return true;
    }

    private ServicesInProgress findServiceInProgress(Integer id) {
        return servicesInProgressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ServicesInProgress not found with id: " + id));
    }

    private Worker findWorker(Integer id) {
        return workerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Worker not found with id: " + id));
    }

    private TaskStatus findTaskStatus(Integer id) {
        return taskStatusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TaskStatus not found with id: " + id));
    }

    public List<Task> getTasksByWorker(Integer workerId) {
        return taskRepository.findAllByAssignedWorkerId(workerId);
    }

    public List<Task> getTasksByServiceInProgress(Integer serviceInProgressId) {
        return taskRepository.findAllByServiceInProgress_Id(serviceInProgressId);
    }

    public List<Task> getTasksByServiceInProgressIds(List<Integer> ids) {
        return taskRepository.findAllByServiceInProgress_IdIn(ids);
    }

    public List<Task> getTasksFromActiveProjects() {
        return taskRepository.findAllTasksWithActiveProjects();
    }


    public Page<Task> findTasks(PaginatedTasksInput in) {
        Sort sort = (in.sortField() != null && in.sortDirection() != null)
                ? Sort.by(Sort.Direction.valueOf(in.sortDirection().name()),
                in.sortField().getFieldName())
                : Sort.unsorted();

        PageRequest pr = PageRequest.of(in.page(), in.size(), sort);
        Specification<Task> spec = TaskSpecifications.withFilters(in.filter());

        return taskRepository.findAll(spec, pr);
    }

    public Page<Task> findTasksByWorker(int workerId, PaginatedTasksInput in) {
        Specification<Task> spec = TaskSpecifications.withFilters(in.filter())
                .and((root, q, cb) -> cb.equal(root.get("assignedWorker").get("id"), workerId));

        Sort sort = (in.sortField() != null && in.sortDirection() != null)
                ? Sort.by(Sort.Direction.valueOf(in.sortDirection().name()),
                in.sortField().getFieldName())
                : Sort.unsorted();

        return taskRepository.findAll(spec, PageRequest.of(in.page(), in.size(), sort));
    }

    @Transactional
    public Task updateTaskStatus(Integer taskId, TaskEvent event) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + taskId));

//        switch (event) {
//            case START -> {
//                if (task.getStartDate() == null) {
//                    task.setStartDate(Instant.from(Instant.now()));
//                    taskRepository.save(task);
//                }
//            }
//            case COMPLETE -> {
//                task.setEndDate(Instant.now());
//                taskRepository.save(task);
//            }
//            case RESUME -> {
//                if (task.getEndDate() != null) {
//                    task.setEndDate(null);
//                    taskRepository.save(task);
//                }
//            }
//        }

        Task resultTask =  taskWorkflowService.transition(taskId, event);
        logTaskAction(AuditAction.UPDATE, resultTask);
        return resultTask;
    }

    private void logTaskAction(AuditAction action, Task task) {
        var user = UserContextHolder.get();

        AuditLog log = AuditLog.builder()
                .workerId(user.getWorkerId())
                .username(user.getUsername())
                .role(user.getRole())
                .action(action)
                .entity(AuditEntity.TASK)
                .description("Task " + action + ": " + task.getName())
                .projectId(task.getServiceInProgress() != null &&
                        task.getServiceInProgress().getProjectService() != null &&
                        task.getServiceInProgress().getProjectService().getProject() != null
                        ? task.getServiceInProgress().getProjectService().getProject().getId() : null)
                .taskId(task.getId())
                .materialId(null)
                .materialReviewId(null)
                .timestamp(Instant.now())
                .build();

        eventPublisher.publishEvent(new AuditLogEvent(this, log));
    }

}
