package org.example.advertisingagency.util.export;

import org.example.advertisingagency.model.Material;

import java.util.List;

public class CsvUtil {
    public static String convertToCsv(List<Material> materials) {
        StringBuilder sb = new StringBuilder("Name,Description,Status\n");
        for (Material m : materials) {
            sb.append("\"").append(m.getName()).append("\",")
                    .append("\"").append(m.getDescription()).append("\",")
                    .append(m.getStatus()).append("\n");
        }
        return sb.toString();
    }
}