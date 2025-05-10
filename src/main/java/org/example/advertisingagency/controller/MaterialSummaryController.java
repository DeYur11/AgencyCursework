package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.material.summary.CreateMaterialSummaryInput;
import org.example.advertisingagency.dto.material.summary.UpdateMaterialSummaryInput;
import org.example.advertisingagency.model.MaterialReview;
import org.example.advertisingagency.model.MaterialSummary;
import org.example.advertisingagency.service.material.MaterialSummaryService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
public class MaterialSummaryController {

    private final MaterialSummaryService materialSummaryService;

    public MaterialSummaryController(MaterialSummaryService materialSummaryService) {
        this.materialSummaryService = materialSummaryService;
    }

    @QueryMapping
    public MaterialSummary materialSummary(@Argument Integer id) {
        return materialSummaryService.getMaterialSummaryById(id);
    }

    @QueryMapping
    public List<MaterialSummary> materialSummaries() {
        return materialSummaryService.getAllMaterialSummaries();
    }

    @MutationMapping
    @Transactional
    public MaterialSummary createMaterialSummary(@Argument CreateMaterialSummaryInput input) {
        return materialSummaryService.createMaterialSummary(input);
    }

    @MutationMapping
    @Transactional
    public MaterialSummary updateMaterialSummary(@Argument Integer id, @Argument UpdateMaterialSummaryInput input) {
        return materialSummaryService.updateMaterialSummary(id, input);
    }

    @MutationMapping
    @Transactional
    public boolean deleteMaterialSummary(@Argument Integer id) {
        return materialSummaryService.deleteMaterialSummary(id);
    }

    @SchemaMapping(typeName = "MaterialSummary", field = "materialReviews")
    public List<MaterialReview> materialReviews(MaterialSummary materialSummary) {
        return materialSummaryService.getMaterialReviewsBySummary(materialSummary.getId());
    }
}
