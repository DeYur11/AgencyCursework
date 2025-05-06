package org.example.advertisingagency.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import org.example.advertisingagency.dto.project.ProjectServiceFilterInput;
import org.example.advertisingagency.model.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ProjectServiceSpecifications {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static Specification<ProjectService> withFilters(ProjectServiceFilterInput filter) {
        if (filter == null) return Specification.where(null);

        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            // ----- SERVICE -----
            if (filter.serviceNameContains() != null && !filter.serviceNameContains().isBlank()) {
                predicate = cb.and(predicate, cb.like(
                        cb.lower(root.get("service").get("serviceName")),
                        "%" + filter.serviceNameContains().toLowerCase() + "%"));
            }

            if (filter.serviceEstimateCostMin() != null) {
                predicate = cb.and(predicate, cb.ge(
                        root.get("service").get("estimateCost"), filter.serviceEstimateCostMin()));
            }

            if (filter.serviceEstimateCostMax() != null) {
                predicate = cb.and(predicate, cb.le(
                        root.get("service").get("estimateCost"), filter.serviceEstimateCostMax()));
            }

            if (filter.serviceTypeIds() != null && !filter.serviceTypeIds().isEmpty()) {
                predicate = cb.and(predicate, root.get("service").get("serviceType").get("id")
                        .in(filter.serviceTypeIds()));
            }

            // ----- PROJECT -----
            if (filter.projectNameContains() != null && !filter.projectNameContains().isBlank()) {
                predicate = cb.and(predicate, cb.like(
                        cb.lower(root.get("project").get("name")),
                        "%" + filter.projectNameContains().toLowerCase() + "%"));
            }

            if (filter.projectDescriptionContains() != null && !filter.projectDescriptionContains().isBlank()) {
                predicate = cb.and(predicate, cb.like(
                        cb.lower(root.get("project").get("description")),
                        "%" + filter.projectDescriptionContains().toLowerCase() + "%"));
            }

            if (filter.costMin() != null) {
                predicate = cb.and(predicate, cb.ge(root.get("project").get("cost"), filter.costMin()));
            }

            if (filter.costMax() != null) {
                predicate = cb.and(predicate, cb.le(root.get("project").get("cost"), filter.costMax()));
            }

            if (filter.estimateCostMin() != null) {
                predicate = cb.and(predicate, cb.ge(root.get("project").get("estimateCost"), filter.estimateCostMin()));
            }

            if (filter.estimateCostMax() != null) {
                predicate = cb.and(predicate, cb.le(root.get("project").get("estimateCost"), filter.estimateCostMax()));
            }

            if (filter.registrationDateFrom() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(
                        root.get("project").get("registrationDate"), LocalDate.parse(filter.registrationDateFrom(), DATE_FORMAT)));
            }

            if (filter.registrationDateTo() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(
                        root.get("project").get("registrationDate"), LocalDate.parse(filter.registrationDateTo(), DATE_FORMAT)));
            }

            if (filter.startDateFrom() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(
                        root.get("project").get("startDate"), LocalDate.parse(filter.startDateFrom(), DATE_FORMAT)));
            }

            if (filter.startDateTo() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(
                        root.get("project").get("startDate"), LocalDate.parse(filter.startDateTo(), DATE_FORMAT)));
            }

            if (filter.endDateFrom() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(
                        root.get("project").get("endDate"), LocalDate.parse(filter.endDateFrom(), DATE_FORMAT)));
            }

            if (filter.endDateTo() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(
                        root.get("project").get("endDate"), LocalDate.parse(filter.endDateTo(), DATE_FORMAT)));
            }

            if (filter.paymentDeadlineFrom() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(
                        root.get("project").get("paymentDeadline"), LocalDate.parse(filter.paymentDeadlineFrom(), DATE_FORMAT)));
            }

            if (filter.paymentDeadlineTo() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(
                        root.get("project").get("paymentDeadline"), LocalDate.parse(filter.paymentDeadlineTo(), DATE_FORMAT)));
            }

            if (filter.statusIds() != null && !filter.statusIds().isEmpty()) {
                predicate = cb.and(predicate, root.get("project").get("status").get("id").in(filter.statusIds()));
            }

            if (filter.projectTypeIds() != null && !filter.projectTypeIds().isEmpty()) {
                predicate = cb.and(predicate, root.get("project").get("projectType").get("id")
                        .in(filter.projectTypeIds()));
            }

            if (filter.clientIds() != null && !filter.clientIds().isEmpty()) {
                predicate = cb.and(predicate, root.get("project").get("client").get("id").in(filter.clientIds()));
            }

            if (filter.managerIds() != null && !filter.managerIds().isEmpty()) {
                predicate = cb.and(predicate, root.get("project").get("manager").get("id").in(filter.managerIds()));
            }

            // ----- SERVICES IN PROGRESS -----
            Join<ProjectService, ServicesInProgress> sipJoin = root.join("servicesInProgress");

            if (filter.serviceInProgressStatusIds() != null && !filter.serviceInProgressStatusIds().isEmpty()) {
                predicate = cb.and(predicate, sipJoin.get("status").get("id").in(filter.serviceInProgressStatusIds()));
            }

            if (filter.serviceInProgressStartDateFrom() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(
                        sipJoin.get("startDate"), LocalDate.parse(filter.serviceInProgressStartDateFrom(), DATE_FORMAT)));
            }

            if (filter.serviceInProgressStartDateTo() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(
                        sipJoin.get("startDate"), LocalDate.parse(filter.serviceInProgressStartDateTo(), DATE_FORMAT)));
            }

            if (filter.serviceInProgressEndDateFrom() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(
                        sipJoin.get("endDate"), LocalDate.parse(filter.serviceInProgressEndDateFrom(), DATE_FORMAT)));
            }

            if (filter.serviceInProgressEndDateTo() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(
                        sipJoin.get("endDate"), LocalDate.parse(filter.serviceInProgressEndDateTo(), DATE_FORMAT)));
            }

            if (filter.serviceInProgressCostMin() != null) {
                predicate = cb.and(predicate, cb.ge(sipJoin.get("cost"), filter.serviceInProgressCostMin()));
            }

            if (filter.serviceInProgressCostMax() != null) {
                predicate = cb.and(predicate, cb.le(sipJoin.get("cost"), filter.serviceInProgressCostMax()));
            }

            if (Boolean.TRUE.equals(filter.onlyMismatched())) {
                Subquery<Long> countSubquery = query.subquery(Long.class); // COUNT завжди Long
                var subRoot = countSubquery.from(ServicesInProgress.class);
                countSubquery.select(cb.count(subRoot));
                countSubquery.where(cb.equal(subRoot.get("projectService").get("id"), root.get("id")));

                // amount — Integer → кастуємо до Long для порівняння
                predicate = cb.and(predicate, cb.notEqual(cb.toLong(root.get("amount")), countSubquery));
            }


            return predicate;
        };
    }
}
