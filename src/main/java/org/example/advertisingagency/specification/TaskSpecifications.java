package org.example.advertisingagency.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.example.advertisingagency.dto.task.TaskFilterInput;
import org.example.advertisingagency.model.Task;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Collection;

@RequiredArgsConstructor
public class TaskSpecifications {

    public static Specification<Task> withFilters(TaskFilterInput f) {
        if (f == null) return Specification.where(null);

        return (root, query, cb) -> {
            Predicate p = cb.conjunction();

            if (hasText(f.nameContains()))
                p = cb.and(p,
                        cb.like(cb.lower(root.get("name")), "%" + f.nameContains().toLowerCase() + "%"));

            if (hasText(f.descriptionContains()))
                p = cb.and(p,
                        cb.like(cb.lower(root.get("description")), "%" + f.descriptionContains().toLowerCase() + "%"));

            if (notEmpty(f.priorityIn()))
                p = cb.and(p, root.get("priority").in(f.priorityIn()));

            if (notEmpty(f.statusIds()))
                p = cb.and(p, root.get("taskStatus").get("id").in(f.statusIds()));

            p = dateRange(cb, root.get("createDatetime"), f.createdFrom(),  f.createdTo(),  p);
            p = dateRange(cb, root.get("startDate"),      f.startDateFrom(),f.startDateTo(),p);
            p = dateRange(cb, root.get("endDate"),        f.endDateFrom(),  f.endDateTo(),  p);
            p = dateRange(cb, root.get("deadline"),       f.deadlineFrom(), f.deadlineTo(), p);

            if (notEmpty(f.serviceInProgressIds()))
                p = cb.and(p, root.get("serviceInProgress").get("id").in(f.serviceInProgressIds()));

            return p;
        };
    }

    /* ---------- helpers ---------- */
    private static boolean hasText(String s)          { return s != null && !s.isBlank(); }
    private static boolean notEmpty(Collection<?> c)  { return c != null && !c.isEmpty(); }

    private static Predicate dateRange(
            CriteriaBuilder cb, Path<LocalDate> field,
            String from, String to, Predicate base) {
        if (hasText(from))
            base = cb.and(base, cb.greaterThanOrEqualTo(field, LocalDate.parse(from)));
        if (hasText(to))
            base = cb.and(base, cb.lessThanOrEqualTo(field, LocalDate.parse(to)));
        return base;
    }
}
