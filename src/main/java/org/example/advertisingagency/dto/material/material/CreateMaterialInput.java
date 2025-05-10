package org.example.advertisingagency.dto.material.material;

import lombok.Data;


import java.util.List;

@Data
public class CreateMaterialInput {
    private String name;
    private String description;
    private Integer usageRestrictionId;
    private Integer licenceTypeId;
    private Integer targetAudienceId;
    private Integer languageId;
    private Integer materialTypeId;
    private Integer materialStatusId;
    private Integer taskId;
    private List<Integer> keywordIds; // 🔥 додати сюди
}
