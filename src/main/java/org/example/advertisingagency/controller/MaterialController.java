package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.material.material.*;
import org.example.advertisingagency.enums.ExportedFile;
import org.example.advertisingagency.model.Keyword;
import org.example.advertisingagency.model.Material;
import org.example.advertisingagency.model.MaterialKeyword;
import org.example.advertisingagency.model.MaterialReview;
import org.example.advertisingagency.repository.MaterialKeywordRepository;
import org.example.advertisingagency.service.material.MaterialExportService;
import org.example.advertisingagency.service.material.MaterialService;
import org.example.advertisingagency.util.BatchLoaderUtils;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class MaterialController {

    private final MaterialService materialService;
    private final MaterialKeywordRepository materialKeywordRepository;
    private final MaterialExportService materialExportService;

    public MaterialController(MaterialService materialService, MaterialKeywordRepository materialKeywordRepository, MaterialExportService materialExportService) {
        this.materialService = materialService;
        this.materialKeywordRepository = materialKeywordRepository;
        this.materialExportService = materialExportService;
    }

    @QueryMapping
    public Material material(@Argument Integer id) {
        return materialService.getMaterialById(id);
    }

    @QueryMapping
    public List<Material> materialsByTask(@Argument Integer taskId) {
        return materialService.getMaterialsByTask(taskId);
    }


    @QueryMapping
    public List<Material> materials() {
        return materialService.getAllMaterials();
    }

    @MutationMapping
    @Transactional
    public Material createMaterial(@Argument CreateMaterialInput input) {
        return materialService.createMaterial(input);
    }

    @MutationMapping
    @Transactional
    public Material updateMaterial(@Argument Integer id, @Argument UpdateMaterialInput input) {
        return materialService.updateMaterial(id, input);
    }

    @MutationMapping
    @Transactional
    public Boolean deleteMaterial(@Argument Integer id) {
        return materialService.deleteMaterial(id);
    }

    @BatchMapping(typeName = "Material", field = "keywords")
    public Map<Material, List<Keyword>> batchKeywords(List<Material> materials) {
        return materialService.getKeywordsForMaterials(materials);
    }



    @BatchMapping(typeName = "Material", field = "reviews")
    public Map<Material, List<MaterialReview>> reviews(List<Material> materials) {
        return materialService.getReviewsForMaterials(materials);
    }

    @QueryMapping
    public MaterialPage paginatedMaterials(@Argument PaginatedMaterialsInput input) {
        return materialService.getPaginatedMaterials(input);
    }

    @QueryMapping
    public ExportedFile exportMaterials(@Argument ExportMaterialsInput input) {
        return materialExportService.exportMaterials(input);
    }

    @QueryMapping
    public List<Material> materialsByWorker(@Argument Integer workerId) {
        return materialService.getMaterialsByWorkerId(workerId);
    }
}
