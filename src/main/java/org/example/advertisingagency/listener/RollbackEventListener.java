package org.example.advertisingagency.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.advertisingagency.event.RollbackEvent;
import org.example.advertisingagency.exception.RollbackException;
import org.example.advertisingagency.model.Material;
import org.example.advertisingagency.model.MaterialReview;
import org.example.advertisingagency.model.Project;
import org.example.advertisingagency.model.Task;
import org.example.advertisingagency.model.log.AuditAction;
import org.example.advertisingagency.model.log.AuditEntity;
import org.example.advertisingagency.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Listener for handling rollback events.
 */
@Slf4j
@Component
public class RollbackEventListener {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final MaterialRepository materialRepository;
    private final MaterialReviewRepository materialReviewRepository;
    private final ServicesInProgressRepository servicesInProgressRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public RollbackEventListener(
            ProjectRepository projectRepository,
            TaskRepository taskRepository,
            MaterialRepository materialRepository,
            MaterialReviewRepository materialReviewRepository,
            ServicesInProgressRepository servicesInProgressRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.materialRepository = materialRepository;
        this.materialReviewRepository = materialReviewRepository;
        this.servicesInProgressRepository = servicesInProgressRepository;
        this.objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @EventListener
    @Transactional
    public void handleRollbackEvent(RollbackEvent event) {
        log.info("Processing rollback for {} ID: {}, action: {}",
                event.getEntityType(), event.getEntityId(), event.getRollbackAction());

        try {
            switch (event.getEntityType()) {
                case PROJECT -> rollbackProject(event);
                case TASK -> rollbackTask(event);
                case MATERIAL -> rollbackMaterial(event);
                case MATERIAL_REVIEW -> rollbackMaterialReview(event);
                case SERVICES_IN_PROGRESS -> rollbackServicesInProgress(event);
                default -> throw new RollbackException("Unsupported entity type for rollback: " + event.getEntityType());
            }
            log.info("Successfully rolled back {} ID: {}", event.getEntityType(), event.getEntityId());
        } catch (Exception e) {
            log.error("Failed to rollback {} ID: {}", event.getEntityType(), event.getEntityId(), e);
            throw new RollbackException("Rollback failed: " + e.getMessage(), e);
        }
    }

    private void rollbackProject(RollbackEvent event) {
        switch (event.getRollbackAction()) {
            case CREATE -> {
                Project project = convertToEntity(event.getPreviousState(), Project.class);
                projectRepository.save(project);
            }
            case UPDATE -> {
                if (!projectRepository.existsById(event.getEntityId())) {
                    throw new RollbackException("Cannot rollback update - project does not exist: " + event.getEntityId());
                }
                Project project = convertToEntity(event.getPreviousState(), Project.class);
                projectRepository.save(project);
            }
            case DELETE -> {
                // For delete rollback, we actually create the entity again with its previous state
                if (projectRepository.existsById(event.getEntityId())) {
                    throw new RollbackException("Cannot rollback delete - project already exists: " + event.getEntityId());
                }
                Project project = convertToEntity(event.getPreviousState(), Project.class);
                projectRepository.save(project);
            }
        }
    }

    private void rollbackTask(RollbackEvent event) {
        switch (event.getRollbackAction()) {
            case CREATE -> {
                Task task = convertToEntity(event.getPreviousState(), Task.class);
                taskRepository.save(task);
            }
            case UPDATE -> {
                if (!taskRepository.existsById(event.getEntityId())) {
                    throw new RollbackException("Cannot rollback update - task does not exist: " + event.getEntityId());
                }
                Task task = convertToEntity(event.getPreviousState(), Task.class);
                taskRepository.save(task);
            }
            case DELETE -> {
                if (taskRepository.existsById(event.getEntityId())) {
                    throw new RollbackException("Cannot rollback delete - task already exists: " + event.getEntityId());
                }
                Task task = convertToEntity(event.getPreviousState(), Task.class);
                taskRepository.save(task);
            }
        }
    }

    private void rollbackMaterial(RollbackEvent event) {
        switch (event.getRollbackAction()) {
            case CREATE -> {
                Material material = convertToEntity(event.getPreviousState(), Material.class);
                materialRepository.save(material);
            }
            case UPDATE -> {
                if (!materialRepository.existsById(event.getEntityId())) {
                    throw new RollbackException("Cannot rollback update - material does not exist: " + event.getEntityId());
                }
                Material material = convertToEntity(event.getPreviousState(), Material.class);
                materialRepository.save(material);
            }
            case DELETE -> {
                if (materialRepository.existsById(event.getEntityId())) {
                    throw new RollbackException("Cannot rollback delete - material already exists: " + event.getEntityId());
                }
                Material material = convertToEntity(event.getPreviousState(), Material.class);
                materialRepository.save(material);
            }
        }
    }

    private void rollbackMaterialReview(RollbackEvent event) {
        switch (event.getRollbackAction()) {
            case CREATE -> {
                MaterialReview review = convertToEntity(event.getPreviousState(), MaterialReview.class);
                materialReviewRepository.save(review);
            }
            case UPDATE -> {
                if (!materialReviewRepository.existsById(event.getEntityId())) {
                    throw new RollbackException("Cannot rollback update - material review does not exist: " + event.getEntityId());
                }
                MaterialReview review = convertToEntity(event.getPreviousState(), MaterialReview.class);
                materialReviewRepository.save(review);
            }
            case DELETE -> {
                if (materialReviewRepository.existsById(event.getEntityId())) {
                    throw new RollbackException("Cannot rollback delete - material review already exists: " + event.getEntityId());
                }
                MaterialReview review = convertToEntity(event.getPreviousState(), MaterialReview.class);
                materialReviewRepository.save(review);
            }
        }
    }

    private void rollbackServicesInProgress(RollbackEvent event) {
        switch (event.getRollbackAction()) {
            case CREATE -> {
                var service = convertToEntity(event.getPreviousState(), org.example.advertisingagency.model.ServicesInProgress.class);
                servicesInProgressRepository.save(service);
            }
            case UPDATE -> {
                if (!servicesInProgressRepository.existsById(event.getEntityId())) {
                    throw new RollbackException("Cannot rollback update - service in progress does not exist: " + event.getEntityId());
                }
                var service = convertToEntity(event.getPreviousState(), org.example.advertisingagency.model.ServicesInProgress.class);
                servicesInProgressRepository.save(service);
            }
            case DELETE -> {
                if (servicesInProgressRepository.existsById(event.getEntityId())) {
                    throw new RollbackException("Cannot rollback delete - service in progress already exists: " + event.getEntityId());
                }
                var service = convertToEntity(event.getPreviousState(), org.example.advertisingagency.model.ServicesInProgress.class);
                servicesInProgressRepository.save(service);
            }
        }
    }

    /**
     * Convert a map of previous state back to an entity.
     */
    private <T> T convertToEntity(Map<String, Object> stateMap, Class<T> entityClass) {
        try {
            return objectMapper.convertValue(stateMap, entityClass);
        } catch (Exception e) {
            log.error("Failed to convert state map to entity: {}", entityClass.getSimpleName(), e);
            throw new RollbackException("Failed to convert state map to entity: " + entityClass.getSimpleName(), e);
        }
    }
}