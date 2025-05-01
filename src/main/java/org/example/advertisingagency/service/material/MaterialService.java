package org.example.advertisingagency.service.material;


import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.material.material.CreateMaterialInput;
import org.example.advertisingagency.dto.material.material.UpdateMaterialInput;
import org.example.advertisingagency.model.*;
import org.example.advertisingagency.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
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

    public MaterialService(MaterialRepository materialRepository,
                           MaterialKeywordRepository materialKeywordRepository,
                           MaterialTypeRepository materialTypeRepository,
                           MaterialStatusRepository materialStatusRepository,
                           UsageRestrictionRepository usageRestrictionRepository,
                           LicenceTypeRepository licenceTypeRepository,
                           TargetAudienceRepository targetAudienceRepository,
                           LanguageRepository languageRepository,
                           TaskRepository taskRepository, MaterialReviewRepository materialReviewRepository, KeywordRepository keywordRepository) {
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
    }

    public List<Material> getMaterialsByTask(Integer taskId) {
        return materialRepository.findAllByTask_Id(taskId);
    }


    public Material getMaterialById(Integer id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material not found"));
    }

    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    @Transactional
    public Material createMaterial(CreateMaterialInput input) {
        Material material = new Material();
        material.setName(input.getName());
        material.setDescription(input.getDescription());
        material.setCreateDatetime(Instant.now());

        material.setType(findMaterialType(input.getMaterialTypeId()));
        material.setStatus(materialStatusRepository.findByName(STARTING_STATUS).orElse(null));
        material.setUsageRestriction(findUsageRestriction(input.getUsageRestrictionId()));
        material.setLicenceType(findLicenceType(input.getLicenceTypeId()));
        material.setTargetAudience(findTargetAudience(input.getTargetAudienceId()));
        material.setLanguage(findLanguage(input.getLanguageId()));
        material.setTask(findTask(input.getTaskId()));

        material = materialRepository.save(material);

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


        return material;
    }

    @Transactional
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

        material = materialRepository.save(material);

        // 🔄 Оновлення keywords
        if (input.getKeywordIds() != null) {
            // 1. Видалити старі зв’язки
            materialKeywordRepository.deleteAllByMaterialId(material.getId());

            // 2. Додати нові зв’язки
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

        return material;
    }


    @Transactional
    public Boolean deleteMaterial(Integer id) {
        if (!materialRepository.existsById(id)) {
            return false;
        }

        // Спочатку видаляємо залежності
        materialKeywordRepository.deleteByMaterialId(id);

        // Потім видаляємо сам матеріал
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
