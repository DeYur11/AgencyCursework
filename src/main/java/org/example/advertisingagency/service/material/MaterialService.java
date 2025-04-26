package org.example.advertisingagency.service.material;


import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.material.material.CreateMaterialInput;
import org.example.advertisingagency.dto.material.material.UpdateMaterialInput;
import org.example.advertisingagency.model.*;
import org.example.advertisingagency.repository.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaterialService {

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

    public MaterialService(MaterialRepository materialRepository,
                           MaterialKeywordRepository materialKeywordRepository,
                           MaterialTypeRepository materialTypeRepository,
                           MaterialStatusRepository materialStatusRepository,
                           UsageRestrictionRepository usageRestrictionRepository,
                           LicenceTypeRepository licenceTypeRepository,
                           TargetAudienceRepository targetAudienceRepository,
                           LanguageRepository languageRepository,
                           TaskRepository taskRepository, MaterialReviewRepository materialReviewRepository) {
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
    }

    public Material getMaterialById(Integer id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material not found"));
    }

    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    public Material createMaterial(CreateMaterialInput input) {
        Material material = new Material();
        material.setName(input.getName());
        material.setDescription(input.getDescription());
        material.setCreateDatetime(Instant.now());

        material.setType(findMaterialType(input.getTypeId()));
        material.setStatus(findMaterialStatus(input.getStatusId()));
        material.setUsageRestriction(findUsageRestriction(input.getUsageRestrictionId()));
        material.setLicenceType(findLicenceType(input.getLicenceTypeId()));
        material.setTargetAudience(findTargetAudience(input.getTargetAudienceId()));
        material.setLanguage(findLanguage(input.getLanguageId()));
        material.setTask(findTask(input.getTaskId()));

        return materialRepository.save(material);
    }

    public Material updateMaterial(Integer id, UpdateMaterialInput input) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Material not found with id: " + id));

        if (input.getName() != null) material.setName(input.getName());
        if (input.getDescription() != null) material.setDescription(input.getDescription());
        material.setUpdateDatetime(Instant.now());

        if (input.getTypeId() != null) material.setType(findMaterialType(input.getTypeId()));
        if (input.getStatusId() != null) material.setStatus(findMaterialStatus(input.getStatusId()));
        if (input.getUsageRestrictionId() != null) material.setUsageRestriction(findUsageRestriction(input.getUsageRestrictionId()));
        if (input.getLicenceTypeId() != null) material.setLicenceType(findLicenceType(input.getLicenceTypeId()));
        if (input.getTargetAudienceId() != null) material.setTargetAudience(findTargetAudience(input.getTargetAudienceId()));
        if (input.getLanguageId() != null) material.setLanguage(findLanguage(input.getLanguageId()));
        if (input.getTaskId() != null) material.setTask(findTask(input.getTaskId()));

        return materialRepository.save(material);
    }

    public Boolean deleteMaterial(Integer id) {
        if (!materialRepository.existsById(id)) {
            return false;
        }
        materialRepository.deleteById(id);
        return true;
    }

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

    public List<Keyword> getKeywordsForMaterial(Material material) {
        return materialKeywordRepository.findByMaterial(material)
                .stream()
                .map(MaterialKeyword::getKeyword)
                .collect(Collectors.toList());
    }
}
