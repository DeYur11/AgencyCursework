package org.example.advertisingagency.dto.task;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
public class UpdateTaskInput {
    private String name;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private LocalDate deadline;
    private Integer serviceInProgressId;
    private Integer assignedWorkerId;
    private Integer taskStatusId;
    private Short priority;
    private BigDecimal value;
}
