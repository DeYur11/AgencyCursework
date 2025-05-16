package org.example.advertisingagency.listener;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import java.util.Map;

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
            ServicesInProgressRepository servicesInProgressRepository,
            ObjectMapper objectMapper) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.materialRepository = materialRepository;
        this.materialReviewRepository = materialReviewRepository;
        this.servicesInProgressRepository = servicesInProgressRepository;
        this.objectMapper = objectMapper;
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
                Material material = convertToMaterial(event.getPreviousState());
                materialRepository.save(material);
            }
            case UPDATE -> {
                if (!materialRepository.existsById(event.getEntityId())) {
                    throw new RollbackException("Cannot rollback update - material does not exist: " + event.getEntityId());
                }
                Material material = convertToMaterial(event.getPreviousState());
                materialRepository.save(material);
            }
            case DELETE -> {
                if (!materialRepository.existsById(event.getEntityId())) {
                    throw new RollbackException("Cannot rollback delete - material already not exists: " + event.getEntityId());
                }
                materialRepository.deleteById(event.getEntityId());
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
            // Використовуємо готовий objectMapper з конфігурації
            return objectMapper.convertValue(stateMap, entityClass);
        } catch (Exception e) {
            log.error("Failed to convert state map to entity: {}", entityClass.getSimpleName(), e);
            throw new RollbackException("Failed to convert state map to entity: " + entityClass.getSimpleName(), e);
        }
    }

    /**
     * Спеціальний метод для конвертації Material з урахуванням ID-полів.
     */
    private Material convertToMaterial(Map<String, Object> stateMap) {
        try {
            // Спочатку десеріалізуємо базові властивості
            Material material = objectMapper.convertValue(stateMap, Material.class);

            // Обробляємо вкладені об'єкти через ID
            if (stateMap.containsKey("materialType") && stateMap.get("materialType") instanceof Map) {
                Map<String, Object> typeMap = (Map<String, Object>) stateMap.get("materialType");
                if (typeMap.containsKey("id")) {
                    material.setMaterialTypeId((Integer) typeMap.get("id"));
                }
            }

            if (stateMap.containsKey("status") && stateMap.get("status") instanceof Map) {
                Map<String, Object> statusMap = (Map<String, Object>) stateMap.get("status");
                if (statusMap.containsKey("id")) {
                    material.setStatusId((Integer) statusMap.get("id"));
                }
            }

            if (stateMap.containsKey("usageRestriction") && stateMap.get("usageRestriction") instanceof Map) {
                Map<String, Object> restrictionMap = (Map<String, Object>) stateMap.get("usageRestriction");
                if (restrictionMap.containsKey("id")) {
                    material.setUsageRestrictionId((Integer) restrictionMap.get("id"));
                }
            }

            if (stateMap.containsKey("licenceType") && stateMap.get("licenceType") instanceof Map) {
                Map<String, Object> licenceMap = (Map<String, Object>) stateMap.get("licenceType");
                if (licenceMap.containsKey("id")) {
                    material.setLicenceTypeId((Integer) licenceMap.get("id"));
                }
            }

            if (stateMap.containsKey("targetAudience") && stateMap.get("targetAudience") instanceof Map) {
                Map<String, Object> audienceMap = (Map<String, Object>) stateMap.get("targetAudience");
                if (audienceMap.containsKey("id")) {
                    material.setTargetAudienceId((Integer) audienceMap.get("id"));
                }
            }

            if (stateMap.containsKey("language") && stateMap.get("language") instanceof Map) {
                Map<String, Object> langMap = (Map<String, Object>) stateMap.get("language");
                if (langMap.containsKey("id")) {
                    material.setLanguageId((Integer) langMap.get("id"));
                }
            }

            if (stateMap.containsKey("task") && stateMap.get("task") instanceof Map) {
                Map<String, Object> taskMap = (Map<String, Object>) stateMap.get("task");
                if (taskMap.containsKey("id")) {
                    material.setTaskId((Integer) taskMap.get("id"));
                }
            }

            return material;
        } catch (Exception e) {
            log.error("Failed to convert state map to Material entity", e);
            throw new RollbackException("Failed to convert state map to Material entity", e);
        }
    }
}