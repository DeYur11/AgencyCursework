package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.project.CreateProjectInput;
import org.example.advertisingagency.dto.project.UpdateProjectInput;
import org.example.advertisingagency.model.Payment;
import org.example.advertisingagency.model.Project;
import org.example.advertisingagency.model.ProjectService;
import org.example.advertisingagency.repository.PaymentRepository;
import org.example.advertisingagency.service.service.ProjectServiceService;
import org.example.advertisingagency.util.BatchLoaderUtils;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ProjectController {

    private final org.example.advertisingagency.service.project.ProjectService projectService;
    private final PaymentRepository paymentRepository;
    private final ProjectServiceService projectServiceService;

    public ProjectController(org.example.advertisingagency.service.project.ProjectService projectService, PaymentRepository paymentRepository, ProjectServiceService projectServiceService) {
        this.projectService = projectService;
        this.paymentRepository = paymentRepository;
        this.projectServiceService = projectServiceService;
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

        List<Payment> payments = BatchLoaderUtils.loadInBatches(
                projectIds,
                paymentRepository::findAllByProject_IdIn
        );

        Map<Integer, Project> idToProject = projects.stream()
                .collect(Collectors.toMap(Project::getId, p -> p));

        Map<Project, List<Payment>> result = new HashMap<>();

        for (Payment payment : payments) {
            Project project = idToProject.get(payment.getProject().getId());
            result.computeIfAbsent(project, p -> new ArrayList<>()).add(payment);
        }

        for (Project project : projects) {
            result.putIfAbsent(project, List.of());
        }

        return result;
    }

    @SchemaMapping(typeName = "Project", field = "projectServices")
    public List<ProjectService> projectServices(Project project) {
        return projectServiceService.getProjectServicesByProject(project.getId());
    }

    @QueryMapping
    public List<Project> projectsByClient(@Argument Integer clientId) {
        return projectService.getProjectsByClient(clientId);
    }
}
