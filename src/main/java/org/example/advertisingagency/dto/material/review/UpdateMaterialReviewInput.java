package org.example.advertisingagency.dto.material.review;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateMaterialReviewInput {
    private Integer materialId;
    private Integer materialSummaryId;
    private String comments;
    private String suggestedChange;
    private LocalDate reviewDate;
    private Integer reviewerId;
}
