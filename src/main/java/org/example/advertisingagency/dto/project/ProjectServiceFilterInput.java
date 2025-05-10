package org.example.advertisingagency.dto.project;

import java.util.List;

public record ProjectServiceFilterInput(
        String serviceNameContains,
        List<Integer> serviceTypeIds,
        Double serviceEstimateCostMin,
        Double serviceEstimateCostMax,

        String projectNameContains,
        String projectDescriptionContains,
        Double costMin,
        Double costMax,
        Double estimateCostMin,
        Double estimateCostMax,
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
        List<Integer> managerIds,

        List<Integer> serviceInProgressStatusIds,
        String serviceInProgressStartDateFrom,
        String serviceInProgressStartDateTo,
        String serviceInProgressEndDateFrom,
        String serviceInProgressEndDateTo,
        Double serviceInProgressCostMin,
        Double serviceInProgressCostMax,

        Boolean onlyMismatched
) {}
