package org.example.advertisingagency.controller;

import org.dataloader.DataLoader;
import org.example.advertisingagency.dto.project.CreateProjectInput;
import org.example.advertisingagency.dto.project.UpdateProjectInput;
import org.example.advertisingagency.model.Payment;
import org.example.advertisingagency.model.Project;
import org.example.advertisingagency.model.ProjectService;
import org.example.advertisingagency.repository.PaymentRepository;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Controller
public class ProjectController {

    private final org.example.advertisingagency.service.project.ProjectService projectService;
    private final PaymentRepository paymentRepository;

    public ProjectController(org.example.advertisingagency.service.project.ProjectService projectService, PaymentRepository paymentRepository) {
        this.projectService = projectService;
        this.paymentRepository = paymentRepository;
    }

    @QueryMapping
    public Project project(@Argument Integer id) {
        return projectService.getProjectById(id);
    }

    @QueryMapping
    public List<Project> projects() {
        return projectService.getAllProjects();
    }

    @MutationMapping
    @Transactional
    public Project createProject(@Argument CreateProjectInput input) {
        return projectService.createProject(input);
    }

    @MutationMapping
    @Transactional
    public Project updateProject(@Argument Integer id, @Argument UpdateProjectInput input) {
        return projectService.updateProject(id, input);
    }

    @MutationMapping
    @Transactional
    public boolean deleteProject(@Argument Integer id) {
        return projectService.deleteProject(id);
    }

    @BatchMapping(typeName = "Project", field = "payments")
    public Map<Project, List<Payment>> payments(List<Project> projects) {
        List<Integer> projectIds = projects.stream()
                .map(Project::getId)
                .toList();

        Map<Project, List<Payment>> result = new HashMap<>();

        final int batchSize = 1000;

        for (int i = 0; i < projectIds.size(); i += batchSize) {
            List<Integer> batchIds = projectIds.subList(i, Math.min(i + batchSize, projectIds.size()));

            List<Payment> batchPayments = paymentRepository.findAllByProjectID_IdIn(batchIds);

            for (Payment payment : batchPayments) {
                Project project = payment.getProjectID();
                result.computeIfAbsent(project, p -> new ArrayList<>()).add(payment);
            }
        }
        for (Project project : projects) {
            result.putIfAbsent(project, List.of());
        }

        return result;
    }




    @SchemaMapping(typeName = "Project", field = "projectServices")
    public List<ProjectService> projectServices(Project project) {
        return projectService.getProjectServicesForProject(project.getId());
    }
}
