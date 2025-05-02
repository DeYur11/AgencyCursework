package org.example.advertisingagency.specification;

import jakarta.persistence.criteria.Predicate;
import org.example.advertisingagency.dto.material.material.MaterialFilterInput;
import org.example.advertisingagency.model.Material;
import org.springframework.data.jpa.domain.Specification;

public class MaterialSpecifications {

    public static Specification<Material> withFilters(MaterialFilterInput filter) {
        if (filter == null) return Specification.where(null);

        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (filter.nameContains() != null && !filter.nameContains().isBlank()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("name")), "%" + filter.nameContains().toLowerCase() + "%"));
            }

            if (filter.descriptionContains() != null && !filter.descriptionContains().isBlank()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("description")), "%" + filter.descriptionContains().toLowerCase() + "%"));
            }

            if (filter.statusIds() != null && !filter.statusIds().isEmpty()) {
                predicate = cb.and(predicate, root.get("status").get("id").in(filter.statusIds()));
            }

            if (filter.languageIds() != null && !filter.languageIds().isEmpty()) {
                predicate = cb.and(predicate, root.get("language").get("id").in(filter.languageIds()));
            }

            if (filter.typeIds() != null && !filter.typeIds().isEmpty()) {
                predicate = cb.and(predicate, root.get("type").get("id").in(filter.typeIds()));
            }

            if (filter.taskIds() != null && !filter.taskIds().isEmpty()) {
                predicate = cb.and(predicate, root.get("task").get("id").in(filter.taskIds()));
            }

            return predicate;
        };
    }
}
