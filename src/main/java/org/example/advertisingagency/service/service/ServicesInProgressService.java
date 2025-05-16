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
import org.example.advertisingagency.repository.*;
import org.example.advertisingagency.service.auth.UserContextHolder;
import org.example.advertisingagency.service.logs.TransactionLogService;
import org.example.advertisingagency.util.state_machine.service.ProjectWorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ServicesInProgressService {

    private final ServicesInProgressRepository servicesInProgressRepository;
    private final ServiceInProgressStatusRepository serviceInProgressStatusRepository;
    private final ProjectServiceRepository projectServiceRepository;
    private final ProjectWorkflowService projectWorkflowService;
    private final TransactionLogService transactionLogService;

    @Autowired
    public ServicesInProgressService(
            ServicesInProgressRepository servicesInProgressRepository,
            ServiceInProgressStatusRepository serviceInProgressStatusRepository,
            ProjectServiceRepository projectServiceRepository,
            ProjectWorkflowService projectWorkflowService,
            TransactionLogService transactionLogService) {
        this.servicesInProgressRepository = servicesInProgressRepository;
        this.serviceInProgressStatusRepository = serviceInProgressStatusRepository;
        this.projectServiceRepository = projectServiceRepository;
        this.projectWorkflowService = projectWorkflowService;
        this.transactionLogService = transactionLogService;
    }

    public ServicesInProgress getServicesInProgressById(Integer id) {
        return servicesInProgressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ServicesInProgress not found with id: " + id));
    }

    public List<ServicesInProgress> getAllServicesInProgress() {
        return servicesInProgressRepository.findAll();
    }

    @Transactional
    public ServicesInProgress createServicesInProgress(CreateServicesInProgressInput input) {
        // Create new service in progress
        ServicesInProgress services = new ServicesInProgress();
        services.setStartDate(input.getStartDate());
        services.setCost(input.getCost() != null ? BigDecimal.valueOf(input.getCost()) : null);
        services.setStatus(findStatus(1)); // Default status (Not Started)

        if (input.getProjectServiceId() != null) {
            services.setProjectService(findProjectService(input.getProjectServiceId()));
        }

        // Save the service
        ServicesInProgress saved = servicesInProgressRepository.save(services);

        // Update project status if needed
        if (saved.getProjectService() != null) {
            Integer projectId = saved.getProjectService().getProject().getId();
            projectWorkflowService.updateProjectStatusIfNeeded(projectId);
        }

        // Log the transaction
        Map<String, Integer> relatedIds = getRelatedIds(saved);
        transactionLogService.logTransaction(
                AuditEntity.SERVICES_IN_PROGRESS,
                saved.getId(),
                AuditAction.CREATE,
                null, // No previous state for creation
                saved,
                "Service in progress created for project service ID: " +
                        (saved.getProjectService() != null ? saved.getProjectService().getId() : "none"),
                relatedIds
        );

        return saved;
    }

    @Transactional
    public ServicesInProgress updateServicesInProgress(Integer id, UpdateServicesInProgressInput input) {
        // Find service to update
        ServicesInProgress services = servicesInProgressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ServicesInProgress not found with id: " + id));

        // Store previous state for rollback
        ServicesInProgress previousState = cloneServicesInProgress(services);

        // Update service fields
        if (input.getStartDate() != null) services.setStartDate(input.getStartDate());
        if (input.getEndDate() != null) services.setEndDate(input.getEndDate());
        if (input.getCost() != null) services.setCost(BigDecimal.valueOf(input.getCost()));
        if (input.getStatusId() != null) services.setStatus(findStatus(input.getStatusId()));
        if (input.getProjectServiceId() != null) services.setProjectService(findProjectService(input.getProjectServiceId()));

        // Save the updated service
        ServicesInProgress saved = servicesInProgressRepository.save(services);

        // Update project status if needed
        if (saved.getProjectService() != null) {
            Integer projectId = saved.getProjectService().getProject().getId();
            projectWorkflowService.updateProjectStatusIfNeeded(projectId);
        }

        // Log the transaction
        Map<String, Integer> relatedIds = getRelatedIds(saved);
        transactionLogService.logTransaction(
                AuditEntity.SERVICES_IN_PROGRESS,
                saved.getId(),
                AuditAction.UPDATE,
                previousState,
                saved,
                "Service in progress updated for project service ID: " +
                        (saved.getProjectService() != null ? saved.getProjectService().getId() : "none"),
                relatedIds
        );

        return saved;
    }

    public List<ServicesInProgress> getServicesInProgressByProjectServiceId(Integer projectServiceId) {
        return servicesInProgressRepository.findAllByProjectService_Id(projectServiceId);
    }

    @Transactional
    public boolean deleteServicesInProgress(Integer id) {
        // Check if service exists
        Optional<ServicesInProgress> opt = servicesInProgressRepository.findById(id);
        if (opt.isEmpty()) {
            return false;
        }

        ServicesInProgress service = opt.get();
        Integer projectId = service.getProjectService() != null ?
                service.getProjectService().getProject().getId() : null;

        // Store previous state for rollback
        ServicesInProgress previousState = cloneServicesInProgress(service);
        Map<String, Integer> relatedIds = getRelatedIds(service);

        try {
            servicesInProgressRepository.deleteById(id);

            // Log the transaction
            transactionLogService.logTransaction(
                    AuditEntity.SERVICES_IN_PROGRESS,
                    id,
                    AuditAction.DELETE,
                    previousState,
                    null, // No current state after deletion
                    "Service in progress deleted for project service ID: " +
                            (service.getProjectService() != null ? service.getProjectService().getId() : "none"),
                    relatedIds
            );

            // Update project status if needed
            if (projectId != null) {
                projectWorkflowService.updateProjectStatusIfNeeded(projectId);
            }

            return true;

        } catch (DataIntegrityViolationException e) {
            return false;
        }
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

    // Helper method to clone a service in progress for rollback
    private ServicesInProgress cloneServicesInProgress(ServicesInProgress original) {
        ServicesInProgress clone = new ServicesInProgress();
        clone.setId(original.getId());
        clone.setStartDate(original.getStartDate());
        clone.setEndDate(original.getEndDate());
        clone.setCost(original.getCost());
        clone.setStatus(original.getStatus());
        clone.setProjectService(original.getProjectService());
        clone.setCreateDatetime(original.getCreateDatetime());
        clone.setUpdateDatetime(original.getUpdateDatetime());
        return clone;
    }

    // Helper method to get related entity IDs for logging
    private Map<String, Integer> getRelatedIds(ServicesInProgress service) {
        Map<String, Integer> ids = new HashMap<>();
        ids.put("serviceInProgressId", service.getId());

        if (service.getProjectService() != null) {
            ids.put("projectServiceId", service.getProjectService().getId());

            if (service.getProjectService().getProject() != null) {
                ids.put("projectId", service.getProjectService().getProject().getId());
            }

            if (service.getProjectService().getService() != null) {
                ids.put("serviceId", service.getProjectService().getService().getId());
            }
        }

        if (service.getStatus() != null) {
            ids.put("statusId", service.getStatus().getId());
        }

        return ids;
    }
}