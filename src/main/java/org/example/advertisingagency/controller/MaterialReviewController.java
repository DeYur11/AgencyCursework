package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.material.review.CreateMaterialReviewInput;
import org.example.advertisingagency.dto.material.review.UpdateMaterialReviewInput;
import org.example.advertisingagency.model.MaterialReview;
import org.example.advertisingagency.service.material.MaterialReviewService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
public class MaterialReviewController {

    private final MaterialReviewService materialReviewService;

    public MaterialReviewController(MaterialReviewService materialReviewService) {
        this.materialReviewService = materialReviewService;
    }

    @QueryMapping
    public MaterialReview materialReview(@Argument Integer id) {
        return materialReviewService.getMaterialReviewById(id);
    }

    @QueryMapping
    public List<MaterialReview> materialReviews() {
        return materialReviewService.getAllMaterialReviews();
    }

    @MutationMapping
    @Transactional
    public MaterialReview createMaterialReview(@Argument CreateMaterialReviewInput input) {
        return materialReviewService.createMaterialReview(input);
    }

    @MutationMapping
    @Transactional
    public MaterialReview updateMaterialReview(@Argument Integer id, @Argument UpdateMaterialReviewInput input) {
        return materialReviewService.updateMaterialReview(id, input);
    }

    @MutationMapping
    @Transactional
    public boolean deleteMaterialReview(@Argument Integer id) {
        return materialReviewService.deleteMaterialReview(id);
    }
}
