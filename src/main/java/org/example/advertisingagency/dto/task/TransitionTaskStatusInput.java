package org.example.advertisingagency.dto.task;

public record TransitionTaskStatusInput(
        Integer taskId,
        String event // "START", "COMPLETE", "CANCEL"
) {}

