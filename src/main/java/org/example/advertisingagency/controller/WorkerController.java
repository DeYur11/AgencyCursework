package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.worker.CreateWorkerInput;
import org.example.advertisingagency.dto.worker.UpdateWorkerInput;
import org.example.advertisingagency.model.MaterialReview;
import org.example.advertisingagency.model.Project;
import org.example.advertisingagency.model.Task;
import org.example.advertisingagency.model.Worker;
import org.example.advertisingagency.repository.TaskRepository;
import org.example.advertisingagency.repository.WorkerRepository;
import org.example.advertisingagency.service.task.TaskService;
import org.example.advertisingagency.service.user.WorkerService;
import org.example.advertisingagency.util.BatchLoaderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class WorkerController {

    private final WorkerService workerService;
    private final TaskRepository taskRepository;
    private final TaskService taskService;
    private final WorkerRepository workerRepository;

    @Autowired
    public WorkerController(WorkerService workerService, TaskRepository taskRepository, TaskService taskService, WorkerRepository workerRepository) {
        this.workerService = workerService;
        this.taskRepository = taskRepository;
        this.taskService = taskService;
        this.workerRepository = workerRepository;
    }

    // ====== QUERY ======

    @QueryMapping
    public List<Worker> workers() {
        return workerService.getAllWorkers();
    }

    @QueryMapping
    public Worker worker(@Argument Integer id) {
        return workerService.getWorkerById(id);
    }

    // ====== MUTATION ======

    @MutationMapping
    @Transactional
    public Worker createWorker(@Argument CreateWorkerInput input) {
        return workerService.createWorker(input);
    }

    @MutationMapping
    @Transactional
    public Worker updateWorker(@Argument Integer id, @Argument UpdateWorkerInput input) {
        return workerService.updateWorker(id, input);
    }

    @MutationMapping
    @Transactional
    public boolean deleteWorker(@Argument Integer id) {
        return workerService.deleteWorker(id);
    }

    @SchemaMapping(typeName = "Worker", field = "managedProjects")
    public List<Project> getManagedProjects(Worker worker) {
        return workerService.getManagedProjects(worker.getId());
    }

    @SchemaMapping(typeName = "Worker", field = "materialReviews")
    public List<MaterialReview> getMaterialReviews(Worker worker) {
        return workerService.getMaterialReviews(worker.getId());
    }

    @BatchMapping(typeName = "Worker", field = "assignedTasks")
    public Map<Worker, List<Task>> tasksByWorker(List<Worker> workers) {
        List<Integer> workerIds = workers.stream()
                .map(Worker::getId)
                .toList();

        List<Task> tasks = BatchLoaderUtils.loadInBatches(
                workerIds,
                taskRepository::findAllByAssignedWorker_IdIn
        );

        Map<Integer, Worker> idToWorker = workers.stream()
                .collect(Collectors.toMap(Worker::getId, w -> w));

        Map<Worker, List<Task>> result = new HashMap<>();

        for (Task task : tasks) {
            Worker worker = idToWorker.get(task.getAssignedWorker().getId());
            result.computeIfAbsent(worker, w -> new ArrayList<>()).add(task);
        }

        for (Worker worker : workers) {
            result.putIfAbsent(worker, List.of());
        }

        return result;
    }

    @QueryMapping
    public List<Task> tasksByWorker(@Argument Integer workerId) {
        return taskService.getTasksByWorker(workerId);
    }

    @QueryMapping("workersByPosition")
    public List<Worker> worker(@Argument String position) {
        return workerRepository.findByPositionNameIgnoreCase(position);
    }
}
