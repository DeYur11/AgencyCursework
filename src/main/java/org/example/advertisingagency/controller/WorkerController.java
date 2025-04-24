package org.example.advertisingagency.controller;

import org.example.advertisingagency.model.*;
import org.example.advertisingagency.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class WorkerController {

    private final WorkerService workerService;

    @Autowired
    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @QueryMapping
    public List<Worker> workers() {
        return workerService.getAllWorkers();
    }

    @QueryMapping
    public Worker worker(@Argument Integer id) {
        return workerService.getWorkerById(id);
    }

    @MutationMapping
    public Worker createWorker(
            @Argument String name,
            @Argument String surname,
            @Argument String email,
            @Argument String phoneNumber,
            @Argument Integer positionId,
            @Argument Integer officeId,
            @Argument Boolean isReviewer
    ) {
        return workerService.createWorker(name, surname, email, phoneNumber, positionId, officeId, isReviewer);
    }

    @MutationMapping
    public Worker updateWorker(
            @Argument Integer id,
            @Argument String name,
            @Argument String surname,
            @Argument String email,
            @Argument String phoneNumber,
            @Argument Integer positionId,
            @Argument Integer officeId,
            @Argument Boolean isReviewer
    ) {
        return workerService.updateWorker(id, name, surname, email, phoneNumber, positionId, officeId, isReviewer);
    }

    @MutationMapping
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
