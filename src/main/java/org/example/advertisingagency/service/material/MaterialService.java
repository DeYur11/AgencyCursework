package org.example.advertisingagency.service.material;


import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.material.material.CreateMaterialInput;
import org.example.advertisingagency.dto.material.material.MaterialPage;
import org.example.advertisingagency.dto.material.material.PaginatedMaterialsInput;
import org.example.advertisingagency.dto.material.material.UpdateMaterialInput;
import org.example.advertisingagency.dto.project.PageInfo;
import org.example.advertisingagency.event.AuditLogEvent;
import org.example.advertisingagency.model.*;
import org.example.advertisingagency.model.auth.AuthenticatedUserContext;
import org.example.advertisingagency.model.log.AuditAction;
import org.example.advertisingagency.model.log.AuditEntity;
import org.example.advertisingagency.model.log.AuditLog;
import org.example.advertisingagency.repository.*;
import org.example.advertisingagency.service.auth.UserContextHolder;
import org.example.advertisingagency.service.logs.TransactionLogService;
import org.example.advertisingagency.specification.MaterialSpecifications;
import org.example.advertisingagency.util.BatchLoaderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MaterialService {

    private final String STARTING_STATUS = "Draft";

    private final MaterialRepository materialRepository;
    private final MaterialKeywordRepository materialKeywordRepository;
    private final MaterialTypeRepository materialTypeRepository;
    private final MaterialStatusRepository materialStatusRepository;
    private final UsageRestrictionRepository usageRestrictionRepository;
    private final LicenceTypeRepository licenceTypeRepository;
    private final TargetAudienceRepository targetAudienceRepository;
    private final LanguageRepository languageRepository;
    private final TaskRepository taskRepository;
    private final MaterialReviewRepository materialReviewRepository;
    private final KeywordRepository keywordRepository;
    private final TransactionLogService transactionLogService;

    @Autowired
    public MaterialService(
            MaterialRepository materialRepository,
            MaterialKeywordRepository materialKeywordRepository,
            MaterialTypeRepository materialTypeRepository,
            MaterialStatusRepository materialStatusRepository,
            UsageRestrictionRepository usageRestrictionRepository,
            LicenceTypeRepository licenceTypeRepository,
            TargetAudienceRepository targetAudienceRepository,
            LanguageRepository languageRepository,
            TaskRepository taskRepository,
            MaterialReviewRepository materialReviewRepository,
            KeywordRepository keywordRepository,
            TransactionLogService transactionLogService) {
        this.materialRepository = materialRepository;
        this.materialKeywordRepository = materialKeywordRepository;
        this.materialTypeRepository = materialTypeRepository;
        this.materialStatusRepository = materialStatusRepository;
        this.usageRestrictionRepository = usageRestrictionRepository;
        this.licenceTypeRepository = licenceTypeRepository;
        this.targetAudienceRepository = targetAudienceRepository;
        this.languageRepository = languageRepository;
        this.taskRepository = taskRepository;
        this.materialReviewRepository = materialReviewRepository;
        this.keywordRepository = keywordRepository;
        this.transactionLogService = transactionLogService;
    }

    public List<Material> getMaterialsByTask(Integer taskId) {
        return materialRepository.findAllByTask_Id(taskId);
    }

    public Material getMaterialById(Integer id) {
        return materialRepository.findById(id)
                .orElse(null);
    }

    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    @Transactional
    public Material createMaterial(CreateMaterialInput input) {
        // Create a new material with input values
        Material material = new Material();
        material.setName(input.getName());
        material.setDescription(input.getDescription());
        material.setCreateDatetime(Instant.now());
        material.setMaterialType(findMaterialType(input.getMaterialTypeId()));
        material.setStatus(materialStatusRepository.findByName(STARTING_STATUS).orElse(null));
        material.setUsageRestriction(findUsageRestriction(input.getUsageRestrictionId()));
        material.setLicenceType(findLicenceType(input.getLicenceTypeId()));
        material.setTargetAudience(findTargetAudience(input.getTargetAudienceId()));
        material.setLanguage(findLanguage(input.getLanguageId()));
        material.setTask(findTask(input.getTaskId()));

        // Save the material
        material = materialRepository.save(material);
        final Integer materialId = material.getId();

        // Add keywords if provided
        if (input.getKeywordIds() != null && !input.getKeywordIds().isEmpty()) {
            List<Keyword> keywords = keywordRepository.findAllById(input.getKeywordIds());
            for (Keyword keyword : keywords) {
                MaterialKeywordId id = new MaterialKeywordId();
                id.setMaterialID(material.getId());
                id.setKeywordID(keyword.getId());

                MaterialKeyword mk = new MaterialKeyword();
                mk.setId(id);
                mk.setMaterial(material);
                mk.setKeyword(keyword);

                materialKeywordRepository.save(mk);
            }
        }

        // Log the transaction for potential rollback
        Map<String, Integer> relatedIds = getRelatedIds(material);
        transactionLogService.logTransaction(
                AuditEntity.MATERIAL,
                materialId,
                AuditAction.CREATE,
                null,  // No previous state for creation
                material,
                "Material created: " + material.getName(),
                relatedIds
        );

        return material;
    }

    @Transactional
    public Material updateMaterial(Integer id, UpdateMaterialInput input) {
        // Find the material to update
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Material not found with id: " + id));

        // Store the previous state for potential rollback
        Material previousState = cloneMaterial(material);

        // Update material fields
        if (input.getName() != null) material.setName(input.getName());
        if (input.getDescription() != null) material.setDescription(input.getDescription());
        material.setUpdateDatetime(Instant.now());

        if (input.getTypeId() != null) material.setMaterialType(findMaterialType(input.getTypeId()));
        if (input.getStatusId() != null) material.setStatus(findMaterialStatus(input.getStatusId()));
        if (input.getUsageRestrictionId() != null) material.setUsageRestriction(findUsageRestriction(input.getUsageRestrictionId()));
        if (input.getLicenceTypeId() != null) material.setLicenceType(findLicenceType(input.getLicenceTypeId()));
        if (input.getTargetAudienceId() != null) material.setTargetAudience(findTargetAudience(input.getTargetAudienceId()));
        if (input.getLanguageId() != null) material.setLanguage(findLanguage(input.getLanguageId()));
        if (input.getTaskId() != null) material.setTask(findTask(input.getTaskId()));

        // Save the updated material
        material = materialRepository.save(material);

        // Update keywords if provided
        List<MaterialKeyword> previousKeywords = new ArrayList<>();
        if (input.getKeywordIds() != null) {
            // Store previous keywords for potential rollback
            previousKeywords = materialKeywordRepository.findByMaterial(material);

            // Delete existing keyword associations
            materialKeywordRepository.deleteAllByMaterialId(material.getId());

            // Add new keyword associations
            if (!input.getKeywordIds().isEmpty()) {
                List<Keyword> keywords = keywordRepository.findAllById(input.getKeywordIds());
                for (Keyword keyword : keywords) {
                    MaterialKeywordId mkId = new MaterialKeywordId();
                    mkId.setMaterialID(material.getId());
                    mkId.setKeywordID(keyword.getId());

                    MaterialKeyword mk = new MaterialKeyword();
                    mk.setId(mkId);
                    mk.setMaterial(material);
                    mk.setKeyword(keyword);

                    materialKeywordRepository.save(mk);
                }
            }
        }

        // Log the transaction for potential rollback
        Map<String, Integer> relatedIds = getRelatedIds(material);
        transactionLogService.logTransaction(
                AuditEntity.MATERIAL,
                material.getId(),
                AuditAction.UPDATE,
                previousState,
                material,
                "Material updated: " + material.getName(),
                relatedIds
        );

        return material;
    }

    @Transactional
    public Boolean deleteMaterial(Integer id) {
        // Check if the material exists
        Optional<Material> materialOpt = materialRepository.findById(id);
        if (materialOpt.isEmpty()) {
            return false;
        }

        Material material = materialOpt.get();

        // Store the material state before deletion for potential rollback
        Material previousState = cloneMaterial(material);
        List<MaterialKeyword> previousKeywords = materialKeywordRepository.findByMaterial(material);

        // Get related IDs before deletion
        Map<String, Integer> relatedIds = getRelatedIds(material);

        // Delete keyword associations
        materialKeywordRepository.deleteByMaterialId(id);

        // Delete the material
        materialRepository.deleteById(id);

        // Log the transaction for potential rollback
        transactionLogService.logTransaction(
                AuditEntity.MATERIAL,
                id,
                AuditAction.DELETE,
                previousState,
                null, // No current state after deletion
                "Material deleted: " + material.getName(),
                relatedIds
        );

        return true;
    }

    // Helper method to clone a material for storing previous state
    private Material cloneMaterial(Material original) {
        Material clone = new Material();
        clone.setId(original.getId());
        clone.setName(original.getName());
        clone.setDescription(original.getDescription());
        clone.setCreateDatetime(original.getCreateDatetime());
        clone.setUpdateDatetime(original.getUpdateDatetime());
        clone.setMaterialType(original.getMaterialType());
        clone.setStatus(original.getStatus());
        clone.setUsageRestriction(original.getUsageRestriction());
        clone.setLicenceType(original.getLicenceType());
        clone.setTargetAudience(original.getTargetAudience());
        clone.setLanguage(original.getLanguage());
        clone.setTask(original.getTask());
        return clone;
    }

    // Helper method to get related entity IDs for logging
    private Map<String, Integer> getRelatedIds(Material material) {
        Map<String, Integer> ids = new HashMap<>();

        if (material.getTask() != null) {
            ids.put("taskId", material.getTask().getId());

            if (material.getTask().getServiceInProgress() != null) {
                ids.put("serviceInProgressId", material.getTask().getServiceInProgress().getId());

                if (material.getTask().getServiceInProgress().getProjectService() != null &&
                        material.getTask().getServiceInProgress().getProjectService().getProject() != null) {
                    ids.put("projectId", material.getTask().getServiceInProgress().getProjectService().getProject().getId());
                }
            }
        }

        ids.put("materialId", material.getId());

        return ids;
    }

    // The rest of the service methods would be similar to the original implementation
    public List<MaterialReview> getReviewsForMaterial(Material material) {
        return materialReviewRepository.findAllByMaterial_Id(material.getId());
    }

    private MaterialType findMaterialType(Integer id) {
        return id == null ? null : materialTypeRepository.findById(id).orElse(null);
    }

    private MaterialStatus findMaterialStatus(Integer id) {
        return id == null ? null : materialStatusRepository.findById(id).orElse(null);
    }

    private UsageRestriction findUsageRestriction(Integer id) {
        return id == null ? null : usageRestrictionRepository.findById(id).orElse(null);
    }

    private LicenceType findLicenceType(Integer id) {
        return id == null ? null : licenceTypeRepository.findById(id).orElse(null);
    }

    private TargetAudience findTargetAudience(Integer id) {
        return id == null ? null : targetAudienceRepository.findById(id).orElse(null);
    }

    private Language findLanguage(Integer id) {
        return id == null ? null : languageRepository.findById(id).orElse(null);
    }

    private Task findTask(Integer id) {
        return id == null ? null : taskRepository.findById(id).orElse(null);
    }

    public Map<Material, List<Keyword>> getKeywordsForMaterials(List<Material> materials) {
        List<Integer> materialIds = materials.stream()
                .map(Material::getId)
                .distinct()
                .toList();

        List<MaterialKeyword> materialKeywords = BatchLoaderUtils.loadInBatches(
                materialIds,
                materialKeywordRepository::findByMaterialIdIn
        );

        Map<Integer, List<Keyword>> keywordsByMaterialId = materialKeywords.stream()
                .collect(Collectors.groupingBy(
                        mk -> mk.getMaterial().getId(),
                        Collectors.mapping(MaterialKeyword::getKeyword, Collectors.toList())
                ));

        Map<Integer, Material> materialMap = materials.stream()
                .collect(Collectors.toMap(
                        Material::getId,
                        Function.identity(),
                        (a, b) -> a
                ));

        Map<Material, List<Keyword>> result = new LinkedHashMap<>();
        for (Integer id : materialIds) {
            Material material = materialMap.get(id);
            result.put(material, keywordsByMaterialId.getOrDefault(id, List.of()));
        }

        return result;
    }

    public Map<Material, List<MaterialReview>> getReviewsForMaterials(List<Material> materials) {
        // Implementation unchanged
        List<Integer> materialIds = materials.stream()
                .map(Material::getId)
                .distinct()
                .toList();

        List<MaterialReview> allReviews = BatchLoaderUtils.loadInBatches(
                materialIds,
                materialReviewRepository::findByMaterialIdIn
        );

        Map<Integer, List<MaterialReview>> reviewsByMaterialId = allReviews.stream()
                .collect(Collectors.groupingBy(
                        review -> review.getMaterial().getId()
                ));

        Map<Integer, Material> materialMap = materials.stream()
                .collect(Collectors.toMap(
                        Material::getId,
                        Function.identity(),
                        (a, b) -> a
                ));

        Map<Material, List<MaterialReview>> result = new LinkedHashMap<>();
        for (Integer id : materialIds) {
            Material material = materialMap.get(id);
            result.put(material, reviewsByMaterialId.getOrDefault(id, List.of()));
        }

        return result;
    }

    public MaterialPage getPaginatedMaterials(PaginatedMaterialsInput input) {
        // Implementation unchanged
        Sort sort = (input.sortField() != null && input.sortDirection() != null)
                ? Sort.by(Sort.Direction.valueOf(input.sortDirection().name()), input.sortField().name())
                : Sort.unsorted();

        PageRequest pageRequest = PageRequest.of(input.page(), input.size(), sort);
        Specification<Material> spec = MaterialSpecifications.withFilters(input.filter());

        Page<Material> page = materialRepository.findAll(spec, pageRequest);

        PageInfo pageInfo = new PageInfo(
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSize(),
                page.getNumber(),
                page.isFirst(),
                page.isLast(),
                page.getNumberOfElements()
        );

        return new MaterialPage(page.getContent(), pageInfo);
    }

    public List<Material> getMaterialsByWorkerId(Integer workerId) {
        return materialRepository.findAllByAssignedWorkerId(workerId);
    }
}
