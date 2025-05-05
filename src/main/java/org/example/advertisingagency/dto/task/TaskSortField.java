package org.example.advertisingagency.dto.task;

import lombok.Getter;

@Getter
public enum TaskSortField {
    ID("id"),
    NAME("name"),
    DESCRIPTION("description"),
    START_DATE("startDate"),
    END_DATE("endDate"),
    DEADLINE("deadline"),
    PRIORITY("priority"),
    VALUE("value"),
    CREATE_DATETIME("createDatetime"),
    UPDATE_DATETIME("updateDatetime");

    private final String fieldName;

    TaskSortField(String fieldName) {
        this.fieldName = fieldName;
    }

}
