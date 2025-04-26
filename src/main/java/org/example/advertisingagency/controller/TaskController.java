package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.task.CreateTaskInput;
import org.example.advertisingagency.dto.task.UpdateTaskInput;
import org.example.advertisingagency.model.Task;
import org.example.advertisingagency.service.task.TaskService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @QueryMapping
    public Task task(@Argument Integer id) {
        return taskService.getTaskById(id);
    }

    @QueryMapping
    public List<Task> tasks() {
        return taskService.getAllTasks();
    }

    @MutationMapping
    @Transactional
    public Task createTask(@Argument CreateTaskInput input) {
        return taskService.createTask(input);
    }

    @MutationMapping
    @Transactional
    public Task updateTask(@Argument Integer id, @Argument UpdateTaskInput input) {
        return taskService.updateTask(id, input);
    }

    @MutationMapping
    @Transactional
    public boolean deleteTask(@Argument Integer id) {
        return taskService.deleteTask(id);
    }
}
