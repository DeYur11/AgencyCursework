package org.example.advertisingagency.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.example.advertisingagency.dto.project.ProjectServiceFilterInput;
import org.example.advertisingagency.model.*;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.domain.Specification;


public class ProjectServiceSpecifications {

    public static Specification<ProjectService> withFilters(ProjectServiceFilterInput filter) {
        if (filter == null) return Specification.where(null);

        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            // Фільтр за назвою сервісу
            if (filter.serviceNameContains() != null && !filter.serviceNameContains().isBlank()) {
                predicate = cb.and(predicate, cb.like(
                        cb.lower(root.get("service").get("serviceName")),
                        "%" + filter.serviceNameContains().toLowerCase() + "%")
                );
            }

            // estimateCost від
            if (filter.estimateCostMin() != null) {
                predicate = cb.and(predicate, cb.ge(root.get("service").get("estimateCost"), filter.estimateCostMin()));
            }

            // estimateCost до
            if (filter.estimateCostMax() != null) {
                predicate = cb.and(predicate, cb.le(root.get("service").get("estimateCost"), filter.estimateCostMax()));
            }


            // serviceTypeIds
            if (filter.serviceTypeIds() != null && !filter.serviceTypeIds().isEmpty()) {
                predicate = cb.and(predicate, root.get("service").get("serviceType").get("id").in(filter.serviceTypeIds()));
            }

            // clientIds
            if (filter.clientIds() != null && !filter.clientIds().isEmpty()) {
                predicate = cb.and(predicate, root.get("project").get("client").get("id").in(filter.clientIds()));
            }

            return predicate;
        };
    }
}
