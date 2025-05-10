package org.example.advertisingagency.enums;

public enum ProjectStatusType {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED,
    PAUSED,
    CANCELLED;

    public static ProjectStatusType from(String dbValue) {
        return ProjectStatusType.valueOf(dbValue.toUpperCase().replace(" ", "_"));
    }

    public static String toDb(ProjectStatusType type) {
        return switch (type) {
            case NOT_STARTED -> "Not Started";
            case IN_PROGRESS -> "In Progress";
            case COMPLETED -> "Completed";
            case PAUSED -> "Paused";
            case CANCELLED -> "Cancelled";
        };
    }
}
