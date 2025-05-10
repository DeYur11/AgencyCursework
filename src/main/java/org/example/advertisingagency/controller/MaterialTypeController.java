package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.material.type.CreateMaterialTypeInput;
import org.example.advertisingagency.dto.material.type.UpdateMaterialTypeInput;
import org.example.advertisingagency.model.Material;
import org.example.advertisingagency.model.MaterialType;
import org.example.advertisingagency.service.material.MaterialTypeService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
public class MaterialTypeController {

    private final MaterialTypeService materialTypeService;

    public MaterialTypeController(MaterialTypeService materialTypeService) {
        this.materialTypeService = materialTypeService;
    }

    @QueryMapping
    public MaterialType materialType(@Argument Integer id) {
        return materialTypeService.getMaterialTypeById(id);
    }

    @QueryMapping
    public List<MaterialType> materialTypes() {
        return materialTypeService.getAllMaterialTypes();
    }

    @MutationMapping
    @Transactional
    public MaterialType createMaterialType(@Argument CreateMaterialTypeInput input) {
        return materialTypeService.createMaterialType(input);
    }

    @MutationMapping
    @Transactional
    public MaterialType updateMaterialType(@Argument Integer id, @Argument UpdateMaterialTypeInput input) {
        return materialTypeService.updateMaterialType(id, input);
    }

    @MutationMapping
    @Transactional
    public boolean deleteMaterialType(@Argument Integer id) {
        return materialTypeService.deleteMaterialType(id);
    }

    @SchemaMapping(typeName = "MaterialType", field = "materials")
    public List<Material> materials(MaterialType materialType) {
        return materialTypeService.getMaterialsByMaterialType(materialType.getId());
    }
}
