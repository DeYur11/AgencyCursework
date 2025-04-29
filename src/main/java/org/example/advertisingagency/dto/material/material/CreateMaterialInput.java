package org.example.advertisingagency.dto.material.material;

import lombok.Data;

@Data
public class CreateMaterialInput {
    private Integer typeId;
    private String name;
    private String description;
    private Integer usageRestrictionId;
    private Integer licenceTypeId;
    private Integer targetAudienceId;
    private Integer languageId;
    private Integer taskId;
}
