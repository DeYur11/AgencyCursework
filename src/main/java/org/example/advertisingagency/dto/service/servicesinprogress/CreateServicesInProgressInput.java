package org.example.advertisingagency.dto.service.servicesinprogress;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateServicesInProgressInput {
    private Integer projectServiceId;
    private LocalDate startDate;
    private Double cost;
}
