package org.example.advertisingagency.enums;

public record ExportedFile(
        String downloadUrl,
        String filename,
        ExportFormat format
) {}

