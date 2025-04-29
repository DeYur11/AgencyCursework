package org.example.advertisingagency.service.task;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.task.CreateTaskInput;
import org.example.advertisingagency.dto.task.UpdateTaskInput;
import org.example.advertisingagency.model.*;
import org.example.advertisingagency.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ServicesInProgressRepository servicesInProgressRepository;
    private final WorkerRepository workerRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final MaterialKeywordRepository materialKeywordRepository;

    public TaskService(TaskRepository taskRepository,
                       ServicesInProgressRepository servicesInProgressRepository,
                       WorkerRepository workerRepository,
                       TaskStatusRepository taskStatusRepository,
                       MaterialKeywordRepository materialKeywordRepository) {
        this.taskRepository = taskRepository;
        this.servicesInProgressRepository = servicesInProgressRepository;
        this.workerRepository = workerRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.materialKeywordRepository = materialKeywordRepository;
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
            task.setServiceInProgress(findServiceInProgress(input.getServiceInProgressId()));
        }
        if (input.getAssignedWorkerId() != null) {
            task.setAssignedWorker(findWorker(input.getAssignedWorkerId()));
        }
        if (input.getTaskStatusId() != null) {
            task.setTaskStatus(findTaskStatus(input.getTaskStatusId()));
        }
        task.setPriority(input.getPriority());
        task.setValue(input.getValue());
        return taskRepository.save(task);
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
        if (input.getTaskStatusId() != null) task.setTaskStatus(findTaskStatus(input.getTaskStatusId()));
        if (input.getPriority() != null) task.setPriority(input.getPriority());
        if (input.getValue() != null) task.setValue(input.getValue());

        return taskRepository.save(task);
    }

    public boolean deleteTask(Integer id) {
        if (!taskRepository.existsById(id)) {
            return false;
        }
        taskRepository.deleteById(id);
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
}
