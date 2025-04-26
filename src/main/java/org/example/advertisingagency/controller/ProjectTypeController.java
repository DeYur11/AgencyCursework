package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.project.CreateProjectTypeInput;
import org.example.advertisingagency.dto.project.UpdateProjectTypeInput;
import org.example.advertisingagency.model.Project;
import org.example.advertisingagency.model.ProjectType;
import org.example.advertisingagency.service.project.ProjectTypeService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
public class ProjectTypeController {

    private final ProjectTypeService projectTypeService;

    public ProjectTypeController(ProjectTypeService projectTypeService) {
        this.projectTypeService = projectTypeService;
    }

    @QueryMapping
    public ProjectType projectType(@Argument Integer id) {
        return projectTypeService.getProjectTypeById(id);
    }

    @QueryMapping
    public List<ProjectType> projectTypes() {
        return projectTypeService.getAllProjectTypes();
    }

    @MutationMapping
    @Transactional
    public ProjectType createProjectType(@Argument CreateProjectTypeInput input) {
        return projectTypeService.createProjectType(input);
    }

    @MutationMapping
    @Transactional
    public ProjectType updateProjectType(@Argument Integer id, @Argument UpdateProjectTypeInput input) {
        return projectTypeService.updateProjectType(id, input);
    }

    @MutationMapping
    @Transactional
    public boolean deleteProjectType(@Argument Integer id) {
        return projectTypeService.deleteProjectType(id);
    }

    @SchemaMapping(typeName = "ProjectType", field = "projects")
    public List<Project> projects(ProjectType projectType) {
        return projectTypeService.getProjectsByProjectType(projectType.getId());
    }
}
