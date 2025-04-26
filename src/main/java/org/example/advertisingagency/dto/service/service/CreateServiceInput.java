package org.example.advertisingagency.dto.service.service;

import lombok.Data;

@Data
public class CreateServiceInput {
    private Integer duration;
    private Double estimateCost;
    private Integer typeId;
    private String serviceName;
}
