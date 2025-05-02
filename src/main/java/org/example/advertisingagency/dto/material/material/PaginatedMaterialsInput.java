package org.example.advertisingagency.dto.material.material;

import java.util.List;

public record PaginatedMaterialsInput(
        int page,
        int size,
        MaterialSortField sortField,
        SortDirection sortDirection,
        MaterialFilterInput filter
) {}

