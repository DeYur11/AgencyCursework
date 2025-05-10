package org.example.advertisingagency.dto.project;

import java.time.LocalDate;

public class ProjectFilterMapper {
    public static ProjectFilter fromInput(ProjectFilterInput input) {
        if (input == null) return new ProjectFilter();

        ProjectFilter filter = new ProjectFilter();

        filter.setNameContains(input.nameContains());
        filter.setDescriptionContains(input.descriptionContains());
        filter.setMinCost(input.costMin());
        filter.setMaxCost(input.costMax());
        filter.setMinEstimateCost(input.estimateCostMin());
        filter.setMaxEstimateCost(input.estimateCostMax());

        filter.setRegistrationDateFrom(parse(input.registrationDateFrom()));
        filter.setRegistrationDateTo(parse(input.registrationDateTo()));
        filter.setStartDateFrom(parse(input.startDateFrom()));
        filter.setStartDateTo(parse(input.startDateTo()));
        filter.setEndDateFrom(parse(input.endDateFrom()));
        filter.setEndDateTo(parse(input.endDateTo()));
        filter.setPaymentDeadlineBefore(parse(input.paymentDeadlineFrom()));
        filter.setPaymentDeadlineAfter(parse(input.paymentDeadlineTo()));

        filter.setStatusIds(input.statusIds());
        filter.setProjectTypeIds(input.projectTypeIds());
        filter.setClientIds(input.clientIds());
        filter.setManagerIds(input.managerIds());

        return filter;
    }

    private static LocalDate parse(String value) {
        if (value == null || value.isBlank()) return null;
        return LocalDate.parse(value);
    }
}
