package org.example.advertisingagency.service.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.service.servicesinprogress.CreateServicesInProgressInput;
import org.example.advertisingagency.dto.service.servicesinprogress.UpdateServicesInProgressInput;
import org.example.advertisingagency.event.AuditLogEvent;
import org.example.advertisingagency.model.ProjectService;
import org.example.advertisingagency.model.ServiceInProgressStatus;
import org.example.advertisingagency.model.ServicesInProgress;
import org.example.advertisingagency.model.log.AuditAction;
import org.example.advertisingagency.model.log.AuditEntity;
import org.example.advertisingagency.model.log.AuditLog;
import org.example.advertisingagency.repository.ProjectServiceRepository;
import org.example.advertisingagency.repository.ServiceInProgressStatusRepository;
import org.example.advertisingagency.repository.ServicesInProgressRepository;
import org.example.advertisingagency.service.auth.UserContextHolder;
import org.example.advertisingagency.util.state_machine.service.ProjectWorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class ServicesInProgressService {

    private final ServicesInProgressRepository servicesInProgressRepository;
    private final ServiceInProgressStatusRepository serviceInProgressStatusRepository;
    private final ProjectServiceRepository projectServiceRepository;
    private final ProjectWorkflowService projectWorkflowService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public ServicesInProgressService(ServicesInProgressRepository servicesInProgressRepository,
                                     ServiceInProgressStatusRepository serviceInProgressStatusRepository,
                                     ProjectServiceRepository projectServiceRepository, ProjectWorkflowService projectWorkflowService, ApplicationEventPublisher eventPublisher) {
        this.servicesInProgressRepository = servicesInProgressRepository;
        this.serviceInProgressStatusRepository = serviceInProgressStatusRepository;
        this.projectServiceRepository = projectServiceRepository;
        this.projectWorkflowService = projectWorkflowService;
        this.eventPublisher = eventPublisher;
    }

    public ServicesInProgress getServicesInProgressById(Integer id) {
        return servicesInProgressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ServicesInProgress not found with id: " + id));
    }

    public List<ServicesInProgress> getAllServicesInProgress() {
        return servicesInProgressRepository.findAll();
    }

    public ServicesInProgress createServicesInProgress(CreateServicesInProgressInput input) {
        ServicesInProgress services = new ServicesInProgress();
        services.setStartDate(input.getStartDate());
        services.setCost(input.getCost() != null ? BigDecimal.valueOf(input.getCost()) : null);
        services.setStatus(findStatus(1));
        if (input.getProjectServiceId() != null) {
            services.setProjectService(findProjectService(input.getProjectServiceId()));
        }
        ServicesInProgress saved = servicesInProgressRepository.save(services);

        // ► оновлюємо статус проєкту
        if (saved.getProjectService() != null) {
            Integer projectId = saved.getProjectService().getProject().getId();
            projectWorkflowService.updateProjectStatusIfNeeded(projectId);
        }
        logAction(AuditAction.CREATE, saved);
        return saved;
    }

    public ServicesInProgress updateServicesInProgress(Integer id, UpdateServicesInProgressInput input) {
        ServicesInProgress services = servicesInProgressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ServicesInProgress not found with id: " + id));

        if (input.getStartDate() != null) services.setStartDate(input.getStartDate());
        if (input.getEndDate() != null) services.setEndDate(input.getEndDate());
        if (input.getCost() != null) services.setCost(BigDecimal.valueOf(input.getCost()));
        if (input.getStatusId() != null) services.setStatus(findStatus(input.getStatusId()));
        if (input.getProjectServiceId() != null) services.setProjectService(findProjectService(input.getProjectServiceId()));

        ServicesInProgress saved = servicesInProgressRepository.save(services);

        if (saved.getProjectService() != null) {
            Integer projectId = saved.getProjectService().getProject().getId();
            projectWorkflowService.updateProjectStatusIfNeeded(projectId);
        }
        logAction(AuditAction.UPDATE, saved);
        return saved;
    }

    public List<ServicesInProgress> getServicesInProgressByProjectServiceId(Integer projectServiceId) {
        return servicesInProgressRepository.findAllByProjectService_Id(projectServiceId);
    }


    public boolean deleteServicesInProgress(Integer id) {
        return servicesInProgressRepository.findById(id).map(service -> {
            Integer projectId = service.getProjectService().getProject().getId();
            try {
                servicesInProgressRepository.deleteById(id);
                logAction(AuditAction.DELETE, service);
                projectWorkflowService.updateProjectStatusIfNeeded(projectId);
                return true;

            } catch (DataIntegrityViolationException e) {
                return false;
            }
        }).orElse(false);
    }


    private ServiceInProgressStatus findStatus(Integer id) {
        return serviceInProgressStatusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Status not found with id: " + id));
    }

    private ProjectService findProjectService(Integer id) {
        return projectServiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProjectService not found with id: " + id));
    }

    public List<ServicesInProgress> getServicesInProgressByProjectServiceIds(List<Integer> projectServiceIds) {
        return servicesInProgressRepository.findAllByProjectService_IdIn(projectServiceIds);
    }

    private void logAction(AuditAction action, ServicesInProgress services) {
        var user = UserContextHolder.get();

        AuditLog log = AuditLog.builder()
                .workerId(user.getWorkerId())
                .username(user.getUsername())
                .role(user.getRole())
                .action(action)
                .entity(AuditEntity.SERVICES_IN_PROGRESS)
                .description("ServicesInProgress " + action + ": " +
                        (services.getProjectService() != null
                                ? "Проєкт-сервіс ID: " + services.getProjectService().getId()
                                : "Без прив'язки"))
                .projectId(services.getProjectService() != null &&
                        services.getProjectService().getProject() != null
                        ? services.getProjectService().getProject().getId()
                        : null)
                .serviceInProgressId(services.getId())
                .taskId(null)
                .materialId(null)
                .materialReviewId(null)
                .timestamp(Instant.now())
                .build();

        eventPublisher.publishEvent(new AuditLogEvent(this, log));
    }
}
