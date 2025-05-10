package org.example.advertisingagency.dto.material.review;

import lombok.Data;

@Data
public class CreateMaterialReviewInput {
    private Integer materialId;
    private Integer materialSummaryId;
    private String comments;
    private String suggestedChange;
    private String  reviewDate;
    private Integer reviewerId;
}