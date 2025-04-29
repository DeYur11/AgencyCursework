package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.service.projectservice.CreateProjectServiceInput;
import org.example.advertisingagency.dto.service.projectservice.UpdateProjectServiceInput;
import org.example.advertisingagency.model.Project;
import org.example.advertisingagency.model.ProjectService;
import org.example.advertisingagency.model.Service;
import org.example.advertisingagency.model.ServicesInProgress;
import org.example.advertisingagency.service.service.ProjectServiceService;
import org.example.advertisingagency.service.service.ServiceService;
import org.example.advertisingagency.service.service.ServicesInProgressService;
import org.example.advertisingagency.util.BatchLoaderUtils;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ProjectServiceController {

    private final ProjectServiceService projectServiceService;
    private final ServiceService serviceService;
    private final org.example.advertisingagency.service.project.ProjectService projectService;
    private final ServicesInProgressService servicesInProgressService;

    public ProjectServiceController(ProjectServiceService projectServiceService, ServiceService serviceService, org.example.advertisingagency.service.project.ProjectService projectService, ServicesInProgressService servicesInProgressService) {
        this.projectServiceService = projectServiceService;
        this.serviceService = serviceService;
        this.projectService = projectService;
        this.servicesInProgressService = servicesInProgressService;
    }

    @QueryMapping
    public ProjectService projectService(@Argument Integer id) {
        return projectServiceService.getProjectServiceById(id);
    }

    @QueryMapping
    public List<ProjectService> projectServices() {
        return projectServiceService.getAllProjectServices();
    }

    @MutationMapping
    @Transactional
    public ProjectService createProjectService(@Argument CreateProjectServiceInput input) {
        return projectServiceService.createProjectService(input);
    }

    @MutationMapping
    @Transactional
    public ProjectService updateProjectService(@Argument Integer id, @Argument UpdateProjectServiceInput input) {
        return projectServiceService.updateProjectService(id, input);
    }

    @MutationMapping
    @Transactional
    public boolean deleteProjectService(@Argument Integer id) {
        return projectServiceService.deleteProjectService(id);
    }


    @BatchMapping(typeName = "ProjectService", field = "service")
    public Mono<Map<ProjectService, Service>> getServices(List<ProjectService> projectServices) {
        List<Integer> serviceIds = projectServices.stream()
                .map(ps -> ps.getService().getId())
                .toList();

        List<Service> services = BatchLoaderUtils.loadInBatches(serviceIds, serviceService::getServicesByIds);

        Map<Integer, Service> serviceMap = services.stream()
                .collect(Collectors.toMap(Service::getId, s -> s));

        Map<ProjectService, Service> result = projectServices.stream()
                .collect(Collectors.toMap(
                        ps -> ps,
                        ps -> serviceMap.get(ps.getService().getId())
                ));

        return Mono.just(result);
    }

    // === project ===
    @BatchMapping(typeName = "ProjectService", field = "project")
    public Mono<Map<ProjectService, Project>> getProjects(List<ProjectService> projectServices) {
        List<Integer> projectIds = projectServices.stream()
                .map(ps -> ps.getProject().getId())
                .toList();

        List<Project> projects = BatchLoaderUtils.loadInBatches(projectIds, projectService::getProjectsByIds);

        Map<Integer, Project> projectMap = projects.stream()
                .collect(Collectors.toMap(Project::getId, p -> p));

        Map<ProjectService, Project> result = projectServices.stream()
                .collect(Collectors.toMap(
                        ps -> ps,
                        ps -> projectMap.get(ps.getProject().getId())
                ));

        return Mono.just(result);
    }

    // === servicesInProgress ===
    @BatchMapping(typeName = "ProjectService", field = "servicesInProgress")
    public Mono<Map<ProjectService, List<ServicesInProgress>>> getServicesInProgress(List<ProjectService> projectServices) {
        List<Integer> projectServiceIds = projectServices.stream()
                .map(ProjectService::getId)
                .toList();

        List<ServicesInProgress> servicesInProgress = BatchLoaderUtils.loadInBatches(
                projectServiceIds,
                servicesInProgressService::getServicesInProgressByProjectServiceIds
        );

        Map<Integer, List<ServicesInProgress>> grouped = servicesInProgress.stream()
                .collect(Collectors.groupingBy(sip -> sip.getProjectServiceID().getId()));

        Map<ProjectService, List<ServicesInProgress>> result = projectServices.stream()
                .collect(Collectors.toMap(
                        ps -> ps,
                        ps -> grouped.getOrDefault(ps.getId(), List.of())
                ));

        return Mono.just(result);
    }

    @QueryMapping
    public List<ProjectService> projectServicesByProject(@Argument Integer projectId) {
        return projectServiceService.getProjectServicesByProject(projectId);
    }
}
