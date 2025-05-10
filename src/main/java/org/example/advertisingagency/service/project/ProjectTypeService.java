package org.example.advertisingagency.service.project;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.project.CreateProjectTypeInput;
import org.example.advertisingagency.dto.project.UpdateProjectTypeInput;
import org.example.advertisingagency.model.ProjectType;
import org.example.advertisingagency.model.Project;
import org.example.advertisingagency.repository.ProjectTypeRepository;
import org.example.advertisingagency.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectTypeService {

    private final ProjectTypeRepository projectTypeRepository;
    private final ProjectRepository projectRepository;

    public ProjectTypeService(ProjectTypeRepository projectTypeRepository, ProjectRepository projectRepository) {
        this.projectTypeRepository = projectTypeRepository;
        this.projectRepository = projectRepository;
    }

    public ProjectType getProjectTypeById(Integer id) {
        return projectTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProjectType not found with id: " + id));
    }

    public List<ProjectType> getAllProjectTypes() {
        return projectTypeRepository.findAll();
    }

    public ProjectType createProjectType(CreateProjectTypeInput input) {
        ProjectType projectType = new ProjectType();
        projectType.setName(input.getName());
        return projectTypeRepository.save(projectType);
    }

    public ProjectType updateProjectType(Integer id, UpdateProjectTypeInput input) {
        ProjectType projectType = projectTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProjectType not found with id: " + id));

        if (input.getName() != null) {
            projectType.setName(input.getName());
        }

        return projectTypeRepository.save(projectType);
    }

    public boolean deleteProjectType(Integer id) {
        if (!projectTypeRepository.existsById(id)) {
            return false;
        }
        projectTypeRepository.deleteById(id);
        return true;
    }

    public List<Project> getProjectsByProjectType(Integer projectTypeId) {
        return projectRepository.findAllByProjectType_Id(projectTypeId);
    }
}
