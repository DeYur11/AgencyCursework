package org.example.advertisingagency.util.state_machine.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.enums.ProjectStatusType;
import org.example.advertisingagency.model.Project;
import org.example.advertisingagency.model.ProjectService;
import org.example.advertisingagency.model.ServicesInProgress;
import org.example.advertisingagency.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectWorkflowService {

    private final ProjectRepository projectRepository;
    private final ProjectStatusRepository projectStatusRepository;
    private final ServicesInProgressRepository sipRepository;
    private final ProjectServiceRepository projectServiceRepository;

    public ProjectWorkflowService(ProjectRepository projectRepository,
                                  ProjectStatusRepository projectStatusRepository,
                                  ServicesInProgressRepository sipRepository,
                                  ProjectServiceRepository projectServiceRepository) {
        this.projectRepository = projectRepository;
        this.projectStatusRepository = projectStatusRepository;
        this.sipRepository = sipRepository;
        this.projectServiceRepository = projectServiceRepository;
    }

    @Transactional
    public void updateProjectStatusIfNeeded(Integer projectId) {
        List<ProjectService> projectServices = projectServiceRepository.findAllByProject_Id(projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + projectId));



        ProjectStatusType newStatus;
        if (projectServices.isEmpty()) {
            return;
        };

        List<ServicesInProgress> allServices = sipRepository.findAllByProjectService_IdIn(
                projectServices.stream().map(ProjectService::getId).toList()
        );

        if (allServices.isEmpty()){
            newStatus = ProjectStatusType.NOT_STARTED;
            String dbStatus = ProjectStatusType.toDb(newStatus);
            var statusEntity = projectStatusRepository.findByName(dbStatus)
                    .orElseThrow(() -> new EntityNotFoundException("ProjectStatus not found: " + dbStatus));
            project.setStatus(statusEntity);
            projectRepository.save(project);
            return;
        };

        boolean allCompleted = allServices.stream()
                .allMatch(s -> s.getStatus().getName().equalsIgnoreCase("Completed"));

        boolean allNotStarted = allServices.stream()
                .allMatch(s -> s.getStatus().getName().equalsIgnoreCase("Not Started"));

        boolean anyInProgressOrOnHold = allServices.stream()
                .anyMatch(s -> {
                    String name = s.getStatus().getName().toLowerCase();
                    return name.equals("in progress") || name.equals("on hold");
                });

        if (allCompleted) {
            newStatus = ProjectStatusType.COMPLETED;
        } else if (allNotStarted) {
            newStatus = ProjectStatusType.NOT_STARTED;
        } else if (anyInProgressOrOnHold) {
            newStatus = ProjectStatusType.IN_PROGRESS;
        } else {
            return; // No change or unsupported scenario
        }




        String dbStatus = ProjectStatusType.toDb(newStatus);
        if (!project.getStatus().getName().equalsIgnoreCase(dbStatus)) {
            var statusEntity = projectStatusRepository.findByName(dbStatus)
                    .orElseThrow(() -> new EntityNotFoundException("ProjectStatus not found: " + dbStatus));
            project.setStatus(statusEntity);
            projectRepository.save(project);
        }
    }
}