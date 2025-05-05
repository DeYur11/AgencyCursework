package org.example.advertisingagency.dto.task;

import org.example.advertisingagency.dto.SortDirection;
import org.example.advertisingagency.dto.project.PageInfo;
import org.example.advertisingagency.model.Task;

import java.util.List;

// TaskFilterInput.java
public record TaskFilterInput(
        String nameContains,
        String descriptionContains,
        List<Integer> priorityIn,
        List<Integer> statusIds,
        String createdFrom,
        String createdTo,
        String startDateFrom,
        String startDateTo,
        String endDateFrom,
        String endDateTo,
        String deadlineFrom,
        String deadlineTo,
        List<Integer> serviceInProgressIds
) {}

