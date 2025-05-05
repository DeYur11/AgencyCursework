package org.example.advertisingagency.enums;

public enum ServiceStatusType {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

    public static ServiceStatusType from(String dbValue) {
        return ServiceStatusType.valueOf(dbValue.toUpperCase().replace(" ", "_"));
    }

    public static String toDb(ServiceStatusType status) {
        return switch (status) {
            case NOT_STARTED -> "Not Started";
            case IN_PROGRESS -> "In Progress";
            case COMPLETED -> "Completed";
            case CANCELLED -> "Cancelled";
        };
    }
}

