package org.example.advertisingagency.specification;

import jakarta.persistence.criteria.Predicate;
import org.example.advertisingagency.dto.project.ProjectFilter;
import org.example.advertisingagency.model.Project;
import org.springframework.data.jpa.domain.Specification;

public class ProjectSpecifications {

    public static Specification<Project> withFilters(ProjectFilter filter) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (filter.getNameContains() != null && !filter.getNameContains().isBlank()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("name")), "%" + filter.getNameContains().toLowerCase() + "%"));
            }

            if (filter.getDescriptionContains() != null && !filter.getDescriptionContains().isBlank()) {
                predicate = cb.or(predicate, cb.like(cb.lower(root.get("description")), "%" + filter.getDescriptionContains().toLowerCase() + "%"));
            }

            if (filter.getRegistrationDateFrom() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("registrationDate"), filter.getRegistrationDateFrom()));
            }
            if (filter.getRegistrationDateTo() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("registrationDate"), filter.getRegistrationDateTo()));
            }

            if (filter.getStartDateFrom() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("startDate"), filter.getStartDateFrom()));
            }
            if (filter.getStartDateTo() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("startDate"), filter.getStartDateTo()));
            }

            if (filter.getEndDateFrom() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("endDate"), filter.getEndDateFrom()));
            }
            if (filter.getEndDateTo() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("endDate"), filter.getEndDateTo()));
            }

            if (filter.getMinCost() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("cost"), filter.getMinCost()));
            }
            if (filter.getMaxCost() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("cost"), filter.getMaxCost()));
            }

            if (filter.getMinEstimateCost() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("estimateCost"), filter.getMinEstimateCost()));
            }
            if (filter.getMaxEstimateCost() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("estimateCost"), filter.getMaxEstimateCost()));
            }

            if (filter.getPaymentDeadlineAfter() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("paymentDeadline"), filter.getPaymentDeadlineAfter()));
            }
            if (filter.getPaymentDeadlineBefore() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("paymentDeadline"), filter.getPaymentDeadlineBefore()));
            }

            if (filter.getStatusIds() != null && !filter.getStatusIds().isEmpty()) {
                predicate = cb.and(predicate, root.get("status").get("id").in(filter.getStatusIds()));
            }

            if (filter.getProjectTypeIds() != null && !filter.getProjectTypeIds().isEmpty()) {
                predicate = cb.and(predicate, root.get("projectType").get("id").in(filter.getProjectTypeIds()));
            }

            if (filter.getClientIds() != null && !filter.getClientIds().isEmpty()) {
                predicate = cb.and(predicate, root.get("client").get("id").in(filter.getClientIds()));
            }

            if (filter.getManagerIds() != null && !filter.getManagerIds().isEmpty()) {
                predicate = cb.and(predicate, root.get("manager").get("id").in(filter.getManagerIds()));
            }

            return predicate;
        };
    }
}
