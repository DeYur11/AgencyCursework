package org.example.advertisingagency.util.state_machine.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.enums.ProjectStatusType;
import org.example.advertisingagency.model.Project;
import org.example.advertisingagency.model.ProjectService;
import org.example.advertisingagency.model.ServicesInProgress;
import org.example.advertisingagency.repository.ProjectRepository;
import org.example.advertisingagency.repository.ProjectServiceRepository;
import org.example.advertisingagency.repository.ProjectStatusRepository;
import org.example.advertisingagency.repository.ServicesInProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
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

        if (projectServices.isEmpty()) {
            return;
        }

        List<ServicesInProgress> allServices = sipRepository.findAllByProjectService_IdIn(
                projectServices.stream().map(ProjectService::getId).toList()
        );

        ProjectStatusType newStatus;
        if (allServices.isEmpty()) {
            newStatus = ProjectStatusType.NOT_STARTED;
        } else {
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
                // Невизначена комбінація — нічого не міняємо
                return;
            }
        }

        String dbStatus = ProjectStatusType.toDb(newStatus);

        // Встановлюємо дату початку, якщо переходимо в IN_PROGRESS
        if (newStatus == ProjectStatusType.IN_PROGRESS
                && (project.getStartDate() == null
                || project.getStatus().getName().equalsIgnoreCase(ProjectStatusType.NOT_STARTED.name()))) {
            project.setStartDate(LocalDate.now());
        }

        if (newStatus == ProjectStatusType.IN_PROGRESS
                && (project.getEndDate() != null
                || project.getStatus().getName().equalsIgnoreCase(ProjectStatusType.COMPLETED.name()))) {
            project.setEndDate(null);
        }

        // Встановлюємо дату завершення, коли всі сервіси COMPLETED
        if (newStatus == ProjectStatusType.COMPLETED && project.getEndDate() == null) {
            project.setEndDate(LocalDate.now());
        }

        // Змінюємо статус в базі, якщо він відрізняється
        if (!project.getStatus().getName().equalsIgnoreCase(dbStatus)) {
            var statusEntity = projectStatusRepository.findByName(dbStatus)
                    .orElseThrow(() -> new EntityNotFoundException("ProjectStatus not found: " + dbStatus));
            project.setStatus(statusEntity);
            projectRepository.save(project);
        }
    }
}
