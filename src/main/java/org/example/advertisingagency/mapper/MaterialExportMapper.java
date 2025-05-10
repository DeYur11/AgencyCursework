package org.example.advertisingagency.mapper;

import org.example.advertisingagency.dto.export.ExportMaterialDTO;
import org.example.advertisingagency.model.Material;

public class MaterialExportMapper {

    public static ExportMaterialDTO toDTO(Material m) {
        return new ExportMaterialDTO(
                m.getId(),
                m.getName(),
                m.getDescription(),
                m.getType() != null ? m.getType().getName() : null,
                m.getLanguage() != null ? m.getLanguage().getName() : null,
                m.getLicenceType() != null ? m.getLicenceType().getName() : null,
                m.getTargetAudience() != null ? m.getTargetAudience().getName() : null,
                m.getUsageRestriction() != null ? m.getUsageRestriction().getName() : null,
                m.getTask().getAssignedWorker().getId(),
                m.getCreateDatetime()
        );
    }
}