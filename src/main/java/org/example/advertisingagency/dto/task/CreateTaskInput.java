package org.example.advertisingagency.dto.task;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

// TODO Зробити так, щоб всі завдання при додаванні мали статус не почато, але мали дедлайн, потім працівник натискає кнопку

@Data
public class CreateTaskInput {
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
