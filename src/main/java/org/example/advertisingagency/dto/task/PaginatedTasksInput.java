package org.example.advertisingagency.dto.task;

import org.example.advertisingagency.dto.SortDirection;

// PaginatedTasksInput.java
public record PaginatedTasksInput(
        int page,
        int size,
        TaskSortField sortField,
        SortDirection sortDirection,
        TaskFilterInput filter
) {}
