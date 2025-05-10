package org.example.advertisingagency.dto.export;

import java.time.Instant;

public record ExportMaterialDTO(
        Integer id,
        String name,
        String description,
        String type,
        String language,
        String licenceType,
        String targetAudience,
        String usageRestriction,
        Integer createdBy,
        Instant createDatetime
) {}
