package org.example.advertisingagency.dto.project;

import org.example.advertisingagency.dto.SortDirection;
import org.example.advertisingagency.model.Project;

public record PaginatedProjectServicesInput(
        int page,
        int size,
        ProjectServiceSortField sortField,
        SortDirection sortDirection,
        ProjectServiceFilterInput filter
) {}
