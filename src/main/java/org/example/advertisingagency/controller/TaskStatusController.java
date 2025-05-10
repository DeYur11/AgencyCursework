package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.task.status.CreateTaskStatusInput;
import org.example.advertisingagency.dto.task.status.UpdateTaskStatusInput;
import org.example.advertisingagency.model.TaskStatus;
import org.example.advertisingagency.model.Task;
import org.example.advertisingagency.service.task.TaskStatusService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
public class TaskStatusController {

    private final TaskStatusService taskStatusService;

    public TaskStatusController(TaskStatusService taskStatusService) {
        this.taskStatusService = taskStatusService;
    }

    @QueryMapping
    public TaskStatus taskStatus(@Argument Integer id) {
        return taskStatusService.getTaskStatusById(id);
    }

    @QueryMapping
    public List<TaskStatus> taskStatuses() {
        return taskStatusService.getAllTaskStatuses();
    }

    @MutationMapping
    @Transactional
    public TaskStatus createTaskStatus(@Argument CreateTaskStatusInput input) {
        return taskStatusService.createTaskStatus(input);
    }

    @MutationMapping
    @Transactional
    public TaskStatus updateTaskStatus(@Argument Integer id, @Argument UpdateTaskStatusInput input) {
        return taskStatusService.updateTaskStatus(id, input);
    }

    @MutationMapping
    @Transactional
    public boolean deleteTaskStatus(@Argument Integer id) {
        return taskStatusService.deleteTaskStatus(id);
    }

    @SchemaMapping(typeName = "TaskStatus", field = "tasks")
    public List<Task> tasks(TaskStatus taskStatus) {
        return taskStatusService.getTasksByTaskStatus(taskStatus.getId());
    }
}
