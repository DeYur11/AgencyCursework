package org.example.advertisingagency.service.task;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.task.status.CreateTaskStatusInput;
import org.example.advertisingagency.dto.task.status.UpdateTaskStatusInput;
import org.example.advertisingagency.model.TaskStatus;
import org.example.advertisingagency.model.Task;
import org.example.advertisingagency.repository.TaskStatusRepository;
import org.example.advertisingagency.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    private final TaskRepository taskRepository;

    public TaskStatusService(TaskStatusRepository taskStatusRepository, TaskRepository taskRepository) {
        this.taskStatusRepository = taskStatusRepository;
        this.taskRepository = taskRepository;
    }

    public TaskStatus getTaskStatusById(Integer id) {
        return taskStatusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TaskStatus not found with id: " + id));
    }

    public List<TaskStatus> getAllTaskStatuses() {
        return taskStatusRepository.findAll();
    }

    public TaskStatus createTaskStatus(CreateTaskStatusInput input) {
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(input.getName());
        return taskStatusRepository.save(taskStatus);
    }

    public TaskStatus updateTaskStatus(Integer id, UpdateTaskStatusInput input) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TaskStatus not found with id: " + id));

        if (input.getName() != null) {
            taskStatus.setName(input.getName());
        }
        return taskStatusRepository.save(taskStatus);
    }

    public boolean deleteTaskStatus(Integer id) {
        if (!taskStatusRepository.existsById(id)) {
            return false;
        }
        taskStatusRepository.deleteById(id);
        return true;
    }

    public List<Task> getTasksByTaskStatus(Integer taskStatusId) {
        return taskRepository.findAllByTaskStatus_Id(taskStatusId);
    }
}
