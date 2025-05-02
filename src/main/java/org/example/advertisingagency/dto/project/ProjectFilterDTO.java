package org.example.advertisingagency.dto.project;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProjectFilterDTO {
    private String nameContains;
    private Integer statusId;
    private Integer clientId;
    private BigDecimal minCost;
    private BigDecimal maxCost;
}

