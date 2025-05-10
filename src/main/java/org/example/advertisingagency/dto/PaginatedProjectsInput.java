package org.example.advertisingagency.dto;

import org.example.advertisingagency.dto.project.ProjectFilterInput;


public record PaginatedProjectsInput(
        int page,
        int size,
        ProjectSortField sortField,
        SortDirection sortDirection,
        ProjectFilterInput filter
) {}
