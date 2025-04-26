package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.worker.CreateWorkerInput;
import org.example.advertisingagency.dto.worker.UpdateWorkerInput;
import org.example.advertisingagency.model.MaterialReview;
import org.example.advertisingagency.model.Project;
import org.example.advertisingagency.model.Task;
import org.example.advertisingagency.model.Worker;
import org.example.advertisingagency.service.user.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
public class WorkerController {

    private final WorkerService workerService;

    @Autowired
    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
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

    @SchemaMapping(typeName = "Worker", field = "assignedTasks")
    public List<Task> getAssignedTasks(Worker worker) {
        return workerService.getAssignedTasks(worker.getId());
    }

    @SchemaMapping(typeName = "Worker", field = "materialReviews")
    public List<MaterialReview> getMaterialReviews(Worker worker) {
        return workerService.getMaterialReviews(worker.getId());
    }
}
