package org.example.advertisingagency.dto.project;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateProjectInput {
    private String name;
    private LocalDate registrationDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal cost;
    private BigDecimal estimateCost;
    private Integer statusId;
    private Integer typeId;
    private LocalDate paymentDeadline;
    private Integer clientId;
    private Integer managerId;
    private String description;
}
