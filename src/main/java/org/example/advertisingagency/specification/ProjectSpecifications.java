package org.example.advertisingagency.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.example.advertisingagency.dto.project.ProjectFilter;
import org.example.advertisingagency.model.Project;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProjectSpecifications {


    private static Specification<Project> commonFilter(ProjectFilter filter, boolean advancedSearch) {

        return (root, query, cb) -> {

            List<Predicate> andPredicates = new ArrayList<>();

            /* ---------- 1) Текстовий пошук ---------------------------------- */
            if (filter.getNameContains() != null && !filter.getNameContains().isBlank()) {
                String pattern = "%" + filter.getNameContains().toLowerCase() + "%";

                if (advancedSearch) {                // 🔍 складний OR-пошук
                    Join<Object, Object> clientJoin  = root.join("client",  JoinType.LEFT);
                    Join<Object, Object> managerJoin = root.join("manager", JoinType.LEFT);

                    Predicate orPredicate = cb.or(
                            cb.like(cb.lower(root.get("name")),        pattern),                          // Project.name
                            cb.like(cb.lower(root.get("description")), pattern),                          // Project.description
                            cb.like(cb.lower(clientJoin.get("name")),  pattern),                          // Client.name
                            cb.like(                                                                         // ПІБ менеджера
                                    cb.lower(
                                            cb.concat(
                                                    cb.concat(managerJoin.get("name"), " "),
                                                    managerJoin.get("surname"))),
                                    pattern)
                    );
                    andPredicates.add(orPredicate);

                } else {                            // 🔍 простий пошук лише у назві
                    andPredicates.add(cb.like(cb.lower(root.get("name")), pattern));
                }
            }

            /* ---------- 2) Діапазони дат ---------------------------------- */
            if (filter.getRegistrationDateFrom() != null)
                andPredicates.add(cb.greaterThanOrEqualTo(root.get("registrationDate"), filter.getRegistrationDateFrom()));

            if (filter.getRegistrationDateTo() != null)
                andPredicates.add(cb.lessThanOrEqualTo(root.get("registrationDate"), filter.getRegistrationDateTo()));

            if (filter.getStartDateFrom() != null)
                andPredicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), filter.getStartDateFrom()));

            if (filter.getStartDateTo() != null)
                andPredicates.add(cb.lessThanOrEqualTo(root.get("startDate"), filter.getStartDateTo()));

            if (filter.getEndDateFrom() != null)
                andPredicates.add(cb.greaterThanOrEqualTo(root.get("endDate"), filter.getEndDateFrom()));

            if (filter.getEndDateTo() != null)
                andPredicates.add(cb.lessThanOrEqualTo(root.get("endDate"), filter.getEndDateTo()));

            /* ---------- 3) Діапазони сум ----------------------------------- */
            if (filter.getMinCost() != null)
                andPredicates.add(cb.greaterThanOrEqualTo(root.get("cost"), filter.getMinCost()));

            if (filter.getMaxCost() != null)
                andPredicates.add(cb.lessThanOrEqualTo(root.get("cost"), filter.getMaxCost()));

            if (filter.getMinEstimateCost() != null)
                andPredicates.add(cb.greaterThanOrEqualTo(root.get("estimateCost"), filter.getMinEstimateCost()));

            if (filter.getMaxEstimateCost() != null)
                andPredicates.add(cb.lessThanOrEqualTo(root.get("estimateCost"), filter.getMaxEstimateCost()));

            /* ---------- 4) Дедлайни оплат --------------------------------- */
            if (filter.getPaymentDeadlineAfter() != null)
                andPredicates.add(cb.greaterThanOrEqualTo(root.get("paymentDeadline"), filter.getPaymentDeadlineAfter()));

            if (filter.getPaymentDeadlineBefore() != null)
                andPredicates.add(cb.lessThanOrEqualTo(root.get("paymentDeadline"), filter.getPaymentDeadlineBefore()));

            /* ---------- 5) Спискові фільтри (id IN …) ---------------------- */
            if (filter.getStatusIds() != null && !filter.getStatusIds().isEmpty())
                andPredicates.add(root.get("status").get("id").in(filter.getStatusIds()));

            if (filter.getProjectTypeIds() != null && !filter.getProjectTypeIds().isEmpty())
                andPredicates.add(root.get("projectType").get("id").in(filter.getProjectTypeIds()));

            if (filter.getClientIds() != null && !filter.getClientIds().isEmpty())
                andPredicates.add(root.get("client").get("id").in(filter.getClientIds()));

            if (filter.getManagerIds() != null && !filter.getManagerIds().isEmpty())
                andPredicates.add(root.get("manager").get("id").in(filter.getManagerIds()));

            /* ---------- 6) Повертаємо фінальний Predicate ----------------- */
            return cb.and(andPredicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Project> withFiltersSimple(ProjectFilter filter) {
        return commonFilter(filter, false);
    }


    public static Specification<Project> withFiltersAdvancedSearch(ProjectFilter filter) {
        return commonFilter(filter, true);
    }
}
