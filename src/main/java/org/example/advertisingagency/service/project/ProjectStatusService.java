package org.example.advertisingagency.service.project;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.project.CreateProjectStatusInput;
import org.example.advertisingagency.dto.project.UpdateProjectStatusInput;
import org.example.advertisingagency.model.ProjectStatus;
import org.example.advertisingagency.model.Project;
import org.example.advertisingagency.repository.ProjectStatusRepository;
import org.example.advertisingagency.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectStatusService {

    private final ProjectStatusRepository projectStatusRepository;
    private final ProjectRepository projectRepository;

    public ProjectStatusService(ProjectStatusRepository projectStatusRepository, ProjectRepository projectRepository) {
        this.projectStatusRepository = projectStatusRepository;
        this.projectRepository = projectRepository;
    }

    public ProjectStatus getProjectStatusById(Integer id) {
        return projectStatusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProjectStatus not found with id: " + id));
    }

    public List<ProjectStatus> getAllProjectStatuses() {
        return projectStatusRepository.findAll();
    }

    public ProjectStatus createProjectStatus(CreateProjectStatusInput input) {
        ProjectStatus projectStatus = new ProjectStatus();
        projectStatus.setName(input.getName());
        return projectStatusRepository.save(projectStatus);
    }

    public ProjectStatus updateProjectStatus(Integer id, UpdateProjectStatusInput input) {
        ProjectStatus projectStatus = projectStatusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProjectStatus not found with id: " + id));

        if (input.getName() != null) {
            projectStatus.setName(input.getName());
        }
        return projectStatusRepository.save(projectStatus);
    }

    public boolean deleteProjectStatus(Integer id) {
        if (!projectStatusRepository.existsById(id)) {
            return false;
        }
        projectStatusRepository.deleteById(id);
        return true;
    }

    public List<Project> getProjectsByProjectStatus(Integer projectStatusId) {
        return projectRepository.findAllByStatus_Id(projectStatusId);
    }
}
