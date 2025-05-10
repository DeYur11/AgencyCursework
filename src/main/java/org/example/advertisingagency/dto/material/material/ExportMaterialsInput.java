package org.example.advertisingagency.dto.material.material;

import org.example.advertisingagency.enums.ExportFormat;

public record ExportMaterialsInput(
        MaterialSortField sortField,
        SortDirection sortDirection,
        MaterialFilterInput filter,
        ExportFormat format
) {}

