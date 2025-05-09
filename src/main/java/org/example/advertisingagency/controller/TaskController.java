package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.project.PageInfo;
import org.example.advertisingagency.dto.task.*;
import org.example.advertisingagency.enums.TaskEvent;
import org.example.advertisingagency.model.Material;
import org.example.advertisingagency.model.Task;
import org.example.advertisingagency.service.material.MaterialService;
import org.example.advertisingagency.service.task.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
public class TaskController {

    private final TaskService taskService;
    private final MaterialService materialService;

    public TaskController(TaskService taskService, MaterialService materialService) {
        this.taskService = taskService;
        this.materialService = materialService;
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

    @QueryMapping
    public List<Task> activeProjectTasks() {
        return taskService.getTasksFromActiveProjects();
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

    @SchemaMapping(typeName = "Task", field = "materials")
    public List<Material> getMaterials(Task task) {
        return materialService.getMaterialsByTask(task.getId());
    }

    @QueryMapping
    public TaskPage paginatedTasksByWorker(@Argument int workerId,
                                           @Argument PaginatedTasksInput input) {
        Page<Task> page = taskService.findTasksByWorker(workerId, input);
        return new TaskPage(page.getContent(), toPageInfo(page));
    }



    @QueryMapping
    public TaskPage paginatedTasks(@Argument PaginatedTasksInput input) {
        Page<Task> page = taskService.findTasks(input);
        return new TaskPage(page.getContent(), toPageInfo(page));
    }

    private PageInfo toPageInfo(Page<Task> page) {
        return new PageInfo(
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSize(),
                page.getNumber(),
                page.isFirst(),
                page.isLast(),
                page.getNumberOfElements()
        );
    }

    @MutationMapping
    @Transactional
    public Task transitionTaskStatus(@Argument TransitionTaskStatusInput input) {
        TaskEvent event;
        try {
            event = TaskEvent.valueOf(input.event().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid task event: " + input.event());
        }

        return taskService.updateTaskStatus(input.taskId(), event);
    }
}
