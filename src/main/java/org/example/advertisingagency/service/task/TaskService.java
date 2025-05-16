package org.example.advertisingagency.service.task;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.task.CreateTaskInput;
import org.example.advertisingagency.dto.task.PaginatedTasksInput;
import org.example.advertisingagency.dto.task.UpdateTaskInput;
import org.example.advertisingagency.enums.TaskEvent;
import org.example.advertisingagency.exception.InvalidStateTransitionException;
import org.example.advertisingagency.model.*;
import org.example.advertisingagency.model.log.AuditAction;
import org.example.advertisingagency.model.log.AuditEntity;
import org.example.advertisingagency.repository.*;
import org.example.advertisingagency.service.logs.TransactionLogService;
import org.example.advertisingagency.specification.TaskSpecifications;
import org.example.advertisingagency.util.state_machine.service.ServiceInProgressWorkflowService;
import org.example.advertisingagency.util.state_machine.service.TaskWorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final WorkerRepository workerRepository;
    private final ServicesInProgressRepository servicesInProgressRepository;
    private final MaterialRepository materialRepository;
    private final TaskWorkflowService taskWorkflowService;
    private final ServiceInProgressWorkflowService serviceInProgressWorkflowService;
    private final TransactionLogService transactionLogService;

    @Autowired
    public TaskService(
            TaskRepository taskRepository,
            TaskStatusRepository taskStatusRepository,
            WorkerRepository workerRepository,
            ServicesInProgressRepository servicesInProgressRepository,
            MaterialRepository materialRepository,
            TaskWorkflowService taskWorkflowService,
            ServiceInProgressWorkflowService serviceInProgressWorkflowService,
            TransactionLogService transactionLogService) {
        this.taskRepository = taskRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.workerRepository = workerRepository;
        this.servicesInProgressRepository = servicesInProgressRepository;
        this.materialRepository = materialRepository;
        this.taskWorkflowService = taskWorkflowService;
        this.serviceInProgressWorkflowService = serviceInProgressWorkflowService;
        this.transactionLogService = transactionLogService;
    }

    public Task getTaskById(Integer id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Transactional
    public Task createTask(CreateTaskInput input) {
        // Create a new task
        Task task = new Task();
        task.setName(input.getName());
        task.setDescription(input.getDescription());
        task.setStartDate(input.getStartDate());
        task.setEndDate(input.getEndDate());
        task.setDeadline(input.getDeadline());

        if (input.getServiceInProgressId() != null) {
            task.setServiceInProgress(findServiceInProgress(input.getServiceInProgressId()));
        }

        if (input.getAssignedWorkerId() != null) {
            task.setAssignedWorker(findWorker(input.getAssignedWorkerId()));
        }

        task.setTaskStatus(findTaskStatus(1)); // Not Started
        task.setPriority(input.getPriority());
        task.setValue(input.getValue());

        // Save the task
        Task savedTask = taskRepository.save(task);

        // Update service status if needed
        if (savedTask.getServiceInProgress() != null) {
            serviceInProgressWorkflowService.updateServiceStatusIfNeeded(savedTask.getServiceInProgress().getId());
        }

        // Log the transaction
        Map<String, Integer> relatedIds = getRelatedIds(savedTask);
        transactionLogService.logTransaction(
                AuditEntity.TASK,
                savedTask.getId(),
                AuditAction.CREATE,
                null, // No previous state for creation
                savedTask,
                "Task created: " + savedTask.getName(),
                relatedIds
        );

        return savedTask;
    }

    @Transactional
    public Task updateTask(Integer id, UpdateTaskInput input) {
        // Find the task to update
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));

        // Store previous state for rollback
        Task previousState = cloneTask(task);

        // Update task fields
        if (input.getName() != null) task.setName(input.getName());
        if (input.getDescription() != null) task.setDescription(input.getDescription());
        if (input.getStartDate() != null) task.setStartDate(input.getStartDate());
        if (input.getEndDate() != null) task.setEndDate(input.getEndDate());
        if (input.getDeadline() != null) task.setDeadline(input.getDeadline());
        if (input.getServiceInProgressId() != null) task.setServiceInProgress(findServiceInProgress(input.getServiceInProgressId()));
        if (input.getAssignedWorkerId() != null) task.setAssignedWorker(findWorker(input.getAssignedWorkerId()));
        if (input.getPriority() != null) task.setPriority(input.getPriority());
        if (input.getValue() != null) task.setValue(input.getValue());

        // Handle status changes through state machine
        Task updatedTask;
        if (input.getTaskStatusId() != null) {
            int currentId = task.getTaskStatus() != null ? task.getTaskStatus().getId() : -1;
            if (input.getTaskStatusId() != currentId) {
                TaskStatus newStatus = findTaskStatus(input.getTaskStatusId());
                TaskEvent event = determineEvent(task.getTaskStatus(), newStatus);
                if (event != null) {
                    updatedTask = taskWorkflowService.transition(id, event); // State machine transition
                } else {
                    throw new InvalidStateTransitionException("Invalid status transition: "
                            + currentId + " → " + input.getTaskStatusId());
                }
            } else {
                updatedTask = taskRepository.save(task);
            }
        } else {
            updatedTask = taskRepository.save(task);
        }

        // Log the transaction
        Map<String, Integer> relatedIds = getRelatedIds(updatedTask);
        transactionLogService.logTransaction(
                AuditEntity.TASK,
                updatedTask.getId(),
                AuditAction.UPDATE,
                previousState,
                updatedTask,
                "Task updated: " + updatedTask.getName(),
                relatedIds
        );

        return updatedTask;
    }

    @Transactional
    public boolean deleteTask(Integer id) {
        // Check if the task exists
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isEmpty()) {
            return false;
        }

        Task task = taskOpt.get();

        // Store previous state for rollback
        Task previousState = cloneTask(task);
        Integer serviceInProgressId = task.getServiceInProgress() != null ?
                task.getServiceInProgress().getId() : null;

        // Get related IDs before deletion
        Map<String, Integer> relatedIds = getRelatedIds(task);

        // Delete the task
        taskRepository.deleteById(id);

        // Update service status if needed
        if (serviceInProgressId != null) {
            serviceInProgressWorkflowService.updateServiceStatusIfNeeded(serviceInProgressId);
        }

        // Log the transaction
        transactionLogService.logTransaction(
                AuditEntity.TASK,
                id,
                AuditAction.DELETE,
                previousState,
                null, // No current state after deletion
                "Task deleted: " + task.getName(),
                relatedIds
        );

        return true;
    }

    @Transactional
    public Task updateTaskStatus(Integer taskId, TaskEvent event) {
        // Find the task
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + taskId));

        // Store previous state for rollback
        Task previousState = cloneTask(task);

        // Perform the state transition
        Task updatedTask = taskWorkflowService.transition(taskId, event);

        // Log the transaction
        Map<String, Integer> relatedIds = getRelatedIds(updatedTask);
        transactionLogService.logTransaction(
                AuditEntity.TASK,
                updatedTask.getId(),
                AuditAction.UPDATE,
                previousState,
                updatedTask,
                "Task status updated: " + updatedTask.getName() + " - Event: " + event,
                relatedIds
        );

        return updatedTask;
    }

    // Additional methods for pagination and filtering
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

    // Helper method to determine the event for status transition
    private TaskEvent determineEvent(TaskStatus current, TaskStatus target) {
        String from = current != null ? current.getName().toLowerCase() : "none";
        String to = target.getName().toLowerCase();

        return switch (from + "->" + to) {
            case "not started->in progress" -> TaskEvent.START;
            case "in progress->on hold"     -> TaskEvent.HOLD;
            case "on hold->in progress", "completed->in progress" -> TaskEvent.RESUME;
            case "in progress->completed"   -> TaskEvent.COMPLETE;
            case "in progress->not started" -> TaskEvent.CANCEL;
            default -> null;
        };
    }

    // Helper method to clone a task for rollback
    private Task cloneTask(Task original) {
        Task clone = new Task();
        clone.setId(original.getId());
        clone.setName(original.getName());
        clone.setDescription(original.getDescription());
        clone.setStartDate(original.getStartDate());
        clone.setEndDate(original.getEndDate());
        clone.setDeadline(original.getDeadline());
        clone.setServiceInProgress(original.getServiceInProgress());
        clone.setAssignedWorker(original.getAssignedWorker());
        clone.setTaskStatus(original.getTaskStatus());
        clone.setPriority(original.getPriority());
        clone.setValue(original.getValue());
        clone.setCreateDatetime(original.getCreateDatetime());
        clone.setUpdateDatetime(original.getUpdateDatetime());
        return clone;
    }

    // Helper methods for finding related entities
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

    // Helper method to get related entity IDs for logging
    private Map<String, Integer> getRelatedIds(Task task) {
        Map<String, Integer> ids = new HashMap<>();
        ids.put("taskId", task.getId());

        if (task.getServiceInProgress() != null) {
            ids.put("serviceInProgressId", task.getServiceInProgress().getId());

            if (task.getServiceInProgress().getProjectService() != null &&
                    task.getServiceInProgress().getProjectService().getProject() != null) {
                ids.put("projectId", task.getServiceInProgress().getProjectService().getProject().getId());
            }
        }

        if (task.getAssignedWorker() != null) {
            ids.put("workerId", task.getAssignedWorker().getId());
        }

        return ids;
    }
}