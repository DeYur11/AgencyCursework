package org.example.advertisingagency.dto.task;

import org.example.advertisingagency.dto.project.PageInfo;
import org.example.advertisingagency.model.Task;

import java.util.List;

// TaskPage.java (той самий PageInfo, що й для проектів/матеріалів)
public record TaskPage(List<Task> content, PageInfo pageInfo) {}
