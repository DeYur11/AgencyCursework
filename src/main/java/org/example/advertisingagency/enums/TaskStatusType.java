package org.example.advertisingagency.enums;

public enum TaskStatusType {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED,
    ON_HOLD;

    public static TaskStatusType from(String statusName) {
        return TaskStatusType.valueOf(statusName.toUpperCase().replace(" ", "_"));
    }

    public static String toDb(TaskStatusType type) {
        return switch (type) {
            case NOT_STARTED -> "Not Started";
            case IN_PROGRESS -> "In Progress";
            case COMPLETED -> "Completed";
            case ON_HOLD -> "On Hold";
        };
    }
}
