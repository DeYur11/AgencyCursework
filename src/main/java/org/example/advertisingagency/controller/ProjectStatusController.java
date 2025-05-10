package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.project.CreateProjectStatusInput;
import org.example.advertisingagency.dto.project.UpdateProjectStatusInput;
import org.example.advertisingagency.model.Project;
import org.example.advertisingagency.model.ProjectStatus;
import org.example.advertisingagency.service.project.ProjectStatusService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
public class ProjectStatusController {

    private final ProjectStatusService projectStatusService;

    public ProjectStatusController(ProjectStatusService projectStatusService) {
        this.projectStatusService = projectStatusService;
    }

    @QueryMapping
    public ProjectStatus projectStatus(@Argument Integer id) {
        return projectStatusService.getProjectStatusById(id);
    }

    @QueryMapping
    public List<ProjectStatus> projectStatuses() {
        return projectStatusService.getAllProjectStatuses();
    }

    @MutationMapping
    @Transactional
    public ProjectStatus createProjectStatus(@Argument CreateProjectStatusInput input) {
        return projectStatusService.createProjectStatus(input);
    }

    @MutationMapping
    @Transactional
    public ProjectStatus updateProjectStatus(@Argument Integer id, @Argument UpdateProjectStatusInput input) {
        return projectStatusService.updateProjectStatus(id, input);
    }

    @MutationMapping
    @Transactional
    public boolean deleteProjectStatus(@Argument Integer id) {
        return projectStatusService.deleteProjectStatus(id);
    }

    @SchemaMapping(typeName = "ProjectStatus", field = "projects")
    public List<Project> projects(ProjectStatus projectStatus) {
        return projectStatusService.getProjectsByProjectStatus(projectStatus.getId());
    }
}
