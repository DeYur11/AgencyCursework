package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.service.projectservice.CreateProjectServiceInput;
import org.example.advertisingagency.dto.service.projectservice.UpdateProjectServiceInput;
import org.example.advertisingagency.model.ProjectService;
import org.example.advertisingagency.model.ServicesInProgress;
import org.example.advertisingagency.service.service.ProjectServiceService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
public class ProjectServiceController {

    private final ProjectServiceService projectServiceService;

    public ProjectServiceController(ProjectServiceService projectServiceService) {
        this.projectServiceService = projectServiceService;
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

    @SchemaMapping(typeName = "ProjectService", field = "servicesInProgress")
    public List<ServicesInProgress> servicesInProgress(ProjectService projectService) {
        return projectServiceService.getServicesInProgressByProjectService(projectService.getId());
    }
}
