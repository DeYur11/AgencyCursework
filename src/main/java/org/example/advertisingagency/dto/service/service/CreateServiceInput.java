package org.example.advertisingagency.dto.service.service;

import lombok.Data;

@Data
public class CreateServiceInput {
    private Double estimateCost;
    private Integer serviceTypeId;
    private String serviceName;
}
