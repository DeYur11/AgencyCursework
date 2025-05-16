package org.example.advertisingagency.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.advertisingagency.model.*;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for serializing and deserializing entity state
 * for transaction logging and rollbacks.
 */
@Slf4j
@Component
public class EntityStateConverter {

    private final ObjectMapper entityMapper;

    public EntityStateConverter(@Qualifier("entityMapper") ObjectMapper entityMapper) {
        this.entityMapper = entityMapper;

        // Configure mapper for entity state conversion
        this.entityMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Convert an entity to a map representation that can be stored in MongoDB
     * for transaction logging.
     */
    public Map<String, Object> convertEntityToMap(Object entity) {
        if (entity == null) {
            return new HashMap<>();
        }

        try {
            // Handle Hibernate proxies
            if (entity instanceof HibernateProxy) {
                LazyInitializer initializer = ((HibernateProxy) entity).getHibernateLazyInitializer();
                entity = initializer.getImplementation();
            }

            // First convert to JSON string to break circular references
            String json = entityMapper.writeValueAsString(entity);

            // Then convert back to Map
            return entityMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("Error converting entity to map: {}", entity.getClass().getSimpleName(), e);

            // Return a simple map with id if possible
            try {
                Map<String, Object> fallback = new HashMap<>();
                if (entity instanceof Material) {
                    fallback.put("id", ((Material) entity).getId());
                    fallback.put("_entityType", "Material");
                } else if (entity instanceof Task) {
                    fallback.put("id", ((Task) entity).getId());
                    fallback.put("_entityType", "Task");
                } else if (entity instanceof Project) {
                    fallback.put("id", ((Project) entity).getId());
                    fallback.put("_entityType", "Project");
                } else if (entity instanceof ServicesInProgress) {
                    fallback.put("id", ((ServicesInProgress) entity).getId());
                    fallback.put("_entityType", "ServicesInProgress");
                } else if (entity instanceof MaterialReview) {
                    fallback.put("id", ((MaterialReview) entity).getId());
                    fallback.put("_entityType", "MaterialReview");
                }
                return fallback;
            } catch (Exception ex) {
                return new HashMap<>();
            }
        }
    }

    /**
     * Convert a map back to an entity for rollback.
     */
    public <T> T convertMapToEntity(Map<String, Object> map, Class<T> entityClass) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        try {
            // First convert to JSON to handle the type conversion properly
            String json = entityMapper.writeValueAsString(map);

            // Then convert to the entity class
            return entityMapper.readValue(json, entityClass);
        } catch (Exception e) {
            log.error("Error converting map to entity: {}", entityClass.getSimpleName(), e);
            return null;
        }
    }

    /**
     * Convert a map specifically to a Material entity, handling
     * all its relationship IDs properly.
     */
    public Material convertMapToMaterial(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        try {
            // Start with basic conversion
            Material material = convertMapToEntity(map, Material.class);

            // Handle relationships based on extracted IDs
            setMaterialRelationships(material, map);

            return material;
        } catch (Exception e) {
            log.error("Error converting map to Material entity", e);
            return null;
        }
    }

    /**
     * Set relationship entities based on IDs extracted from the map.
     */
    @SuppressWarnings("unchecked")
    private void setMaterialRelationships(Material material, Map<String, Object> map) {
        // MaterialType relationship
        if (map.containsKey("materialType") && map.get("materialType") instanceof Map) {
            Map<String, Object> typeMap = (Map<String, Object>) map.get("materialType");
            if (typeMap.containsKey("id")) {
                material.setMaterialTypeId((Integer) typeMap.get("id"));
            }
        }

        // Status relationship
        if (map.containsKey("status") && map.get("status") instanceof Map) {
            Map<String, Object> statusMap = (Map<String, Object>) map.get("status");
            if (statusMap.containsKey("id")) {
                material.setStatusId((Integer) statusMap.get("id"));
            }
        }

        // UsageRestriction relationship
        if (map.containsKey("usageRestriction") && map.get("usageRestriction") instanceof Map) {
            Map<String, Object> restrictionMap = (Map<String, Object>) map.get("usageRestriction");
            if (restrictionMap.containsKey("id")) {
                material.setUsageRestrictionId((Integer) restrictionMap.get("id"));
            }
        }

        // LicenceType relationship
        if (map.containsKey("licenceType") && map.get("licenceType") instanceof Map) {
            Map<String, Object> licenceMap = (Map<String, Object>) map.get("licenceType");
            if (licenceMap.containsKey("id")) {
                material.setLicenceTypeId((Integer) licenceMap.get("id"));
            }
        }

        // TargetAudience relationship
        if (map.containsKey("targetAudience") && map.get("targetAudience") instanceof Map) {
            Map<String, Object> audienceMap = (Map<String, Object>) map.get("targetAudience");
            if (audienceMap.containsKey("id")) {
                material.setTargetAudienceId((Integer) audienceMap.get("id"));
            }
        }

        // Language relationship
        if (map.containsKey("language") && map.get("language") instanceof Map) {
            Map<String, Object> langMap = (Map<String, Object>) map.get("language");
            if (langMap.containsKey("id")) {
                material.setLanguageId((Integer) langMap.get("id"));
            }
        }

        // Task relationship
        if (map.containsKey("task") && map.get("task") instanceof Map) {
            Map<String, Object> taskMap = (Map<String, Object>) map.get("task");
            if (taskMap.containsKey("id")) {
                material.setTaskId((Integer) taskMap.get("id"));
            }
        }
    }
}