package org.example.advertisingagency.dto.project;


import java.util.List;

public record ProjectFilterInput(
        String nameContains,
        String descriptionContains,
        Float costMin,
        Float costMax,
        Float estimateCostMin,
        Float estimateCostMax,
        String registrationDateFrom,
        String registrationDateTo,
        String startDateFrom,
        String startDateTo,
        String endDateFrom,
        String endDateTo,
        String paymentDeadlineFrom,
        String paymentDeadlineTo,
        List<Integer> statusIds,
        List<Integer> projectTypeIds,
        List<Integer> clientIds,
        List<Integer> managerIds
) {}
