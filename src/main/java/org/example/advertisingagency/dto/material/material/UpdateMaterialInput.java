package org.example.advertisingagency.dto.material.material;

import lombok.Data;

@Data
public class UpdateMaterialInput {
    private Integer id;
    private Integer typeId;
    private String name;
    private Integer statusId;
    private String description;
    private Integer usageRestrictionId;
    private Integer licenceTypeId;
    private Integer targetAudienceId;
    private Integer languageId;
    private Integer taskId;
}
