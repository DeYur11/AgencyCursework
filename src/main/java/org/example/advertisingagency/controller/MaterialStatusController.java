package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.material.status.CreateMaterialStatusInput;
import org.example.advertisingagency.dto.material.status.UpdateMaterialStatusInput;
import org.example.advertisingagency.model.Material;
import org.example.advertisingagency.model.MaterialStatus;
import org.example.advertisingagency.service.material.MaterialStatusService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
public class MaterialStatusController {

    private final MaterialStatusService materialStatusService;

    public MaterialStatusController(MaterialStatusService materialStatusService) {
        this.materialStatusService = materialStatusService;
    }

    @QueryMapping
    public MaterialStatus materialStatus(@Argument Integer id) {
        return materialStatusService.getMaterialStatusById(id);
    }

    @QueryMapping
    public List<MaterialStatus> materialStatuses() {
        return materialStatusService.getAllMaterialStatuses();
    }

    @MutationMapping
    @Transactional
    public MaterialStatus createMaterialStatus(@Argument CreateMaterialStatusInput input) {
        return materialStatusService.createMaterialStatus(input);
    }

    @MutationMapping
    @Transactional
    public MaterialStatus updateMaterialStatus(@Argument Integer id, @Argument UpdateMaterialStatusInput input) {
        return materialStatusService.updateMaterialStatus(id, input);
    }

    @MutationMapping
    @Transactional
    public boolean deleteMaterialStatus(@Argument Integer id) {
        return materialStatusService.deleteMaterialStatus(id);
    }

    @SchemaMapping(typeName = "MaterialStatus", field = "materials")
    public List<Material> materials(MaterialStatus materialStatus) {
        return materialStatusService.getMaterialsByMaterialStatus(materialStatus.getId());
    }
}
