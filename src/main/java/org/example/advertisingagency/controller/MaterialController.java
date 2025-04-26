package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.material.material.CreateMaterialInput;
import org.example.advertisingagency.dto.material.material.UpdateMaterialInput;
import org.example.advertisingagency.model.Keyword;
import org.example.advertisingagency.model.Material;
import org.example.advertisingagency.model.MaterialReview;
import org.example.advertisingagency.service.material.MaterialService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
public class MaterialController {

    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @QueryMapping
    public Material material(@Argument Integer id) {
        return materialService.getMaterialById(id);
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

    @SchemaMapping(typeName = "Material", field = "keywords")
    public List<Keyword> keywords(Material material) {
        return materialService.getKeywordsForMaterial(material);
    }

    @SchemaMapping(typeName = "Material", field = "reviews")
    public List<MaterialReview> reviews(Material material) {
        return materialService.getReviewsForMaterial(material);
    }
}
