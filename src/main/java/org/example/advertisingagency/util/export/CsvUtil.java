package org.example.advertisingagency.util.export;

import org.example.advertisingagency.dto.export.ExportMaterialDTO;

import java.util.List;

public class CsvUtil {

    public static String convertToCsv(List<ExportMaterialDTO> materials) {
        StringBuilder sb = new StringBuilder(
                "ID,Name,Description,Type,Language,LicenceType,TargetAudience,UsageRestriction,CreatedBy,CreateDatetime\n"
        );

        for (ExportMaterialDTO m : materials) {
            sb.append(m.id()).append(",")
                    .append(csvEscape(m.name())).append(",")
                    .append(csvEscape(m.description())).append(",")
                    .append(csvEscape(m.type())).append(",")
                    .append(csvEscape(m.language())).append(",")
                    .append(csvEscape(m.licenceType())).append(",")
                    .append(csvEscape(m.targetAudience())).append(",")
                    .append(csvEscape(m.usageRestriction())).append(",")
                    .append(csvEscape(String.valueOf(m.createdBy()))).append(",")
                    .append(m.createDatetime()).append("\n");
        }

        return sb.toString();
    }

    private static String csvEscape(String value) {
        if (value == null) return "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
