package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.PaginatedProjectsInput;
import org.example.advertisingagency.dto.project.*;
import org.example.advertisingagency.model.Payment;
import org.example.advertisingagency.model.Project;
import org.example.advertisingagency.model.ProjectService;
import org.example.advertisingagency.repository.PaymentRepository;
import org.example.advertisingagency.repository.ProjectRepository;
import org.example.advertisingagency.service.service.ProjectServiceService;
import org.example.advertisingagency.specification.ProjectSpecifications;
import org.example.advertisingagency.util.BatchLoaderUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
    private final ProjectRepository projectRepository;

    public ProjectController(org.example.advertisingagency.service.project.ProjectService projectService, PaymentRepository paymentRepository, ProjectServiceService projectServiceService, ProjectRepository projectRepository) {
        this.projectService = projectService;
        this.paymentRepository = paymentRepository;
        this.projectServiceService = projectServiceService;
        this.projectRepository = projectRepository;
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
    public Project changeProjectStatus(@Argument Integer projectId, @Argument Integer statusId ) {
        return projectService.updateStatus(projectId, statusId);
    }

    @MutationMapping
    @Transactional
    public boolean deleteProject(@Argument Integer id) {
        return projectService.deleteProject(id);
    }

    @MutationMapping
    public Project pauseProject(@Argument Integer projectId) {
        return new Project();
    }

    @MutationMapping
    public Project resumeProject(@Argument Integer projectId) {
        return new Project();
    }

    @MutationMapping
    public Project cancelProject(@Argument Integer projectId) {
        return new Project();
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
    public List<Project> projectsByManager(@Argument Integer managerId) {
        return projectService.getProjectByProjectManager(managerId);
    }

    @QueryMapping
    public List<Project> projectsByClient(@Argument Integer clientId) {
        return projectService.getProjectsByClient(clientId);
    }

    @QueryMapping
    public ProjectPage paginatedProjects(@Argument PaginatedProjectsInput input) {Sort sort = (input.sortField() != null && input.sortDirection() != null)
                ? Sort.by(Sort.Direction.valueOf(input.sortDirection().name()), input.sortField().name())
                : Sort.unsorted();

        PageRequest pageRequest = PageRequest.of(input.page(), input.size(), sort);

        Specification<Project> spec = ProjectSpecifications.withFilters(ProjectFilterMapper.fromInput(input.filter()));

        Page<Project> page = projectRepository.findAll(spec, pageRequest);

        PageInfo pageInfo = new PageInfo(
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSize(),
                page.getNumber(),
                page.isFirst(),
                page.isLast(),
                page.getNumberOfElements()
        );

        return new ProjectPage(page.getContent(), pageInfo);
    }


}
