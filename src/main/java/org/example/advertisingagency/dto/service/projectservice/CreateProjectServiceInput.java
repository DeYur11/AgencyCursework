package org.example.advertisingagency.dto.service.projectservice;

import lombok.Data;

@Data
public class CreateProjectServiceInput {
    private Integer serviceId;
    private Integer projectId;
    private Short amount;
}
