package org.example.advertisingagency.dto.service.servicesinprogress;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateServicesInProgressInput {
    private LocalDate startDate;
    private LocalDate endDate;
    private Double cost;
    private Integer statusId;
    private Integer projectServiceId;
}
