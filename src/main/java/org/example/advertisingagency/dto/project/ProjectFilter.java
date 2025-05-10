package org.example.advertisingagency.dto.project;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Data
public class ProjectFilter {
    private String nameContains;
    private String descriptionContains;
    private LocalDate registrationDateFrom;
    private LocalDate registrationDateTo;
    private LocalDate startDateFrom;
    private LocalDate startDateTo;
    private LocalDate endDateFrom;
    private LocalDate endDateTo;
    private LocalDate paymentDeadlineBefore;
    private LocalDate paymentDeadlineAfter;
    private Float minCost;
    private Float maxCost;
    private Float minEstimateCost;
    private Float maxEstimateCost;
    private List<Integer> statusIds;
    private List<Integer> projectTypeIds;
    private List<Integer> clientIds;
    private List<Integer> managerIds;
}
