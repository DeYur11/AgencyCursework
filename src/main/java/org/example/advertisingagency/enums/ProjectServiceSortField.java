package org.example.advertisingagency.enums;

import lombok.Getter;

@Getter
public enum ProjectServiceSortField {
    serviceName("service.serviceName"),
    serviceEstimateCost("service.estimateCost"),
    projectName("project.name"),
    projectCost("project.cost"),
    projectEstimateCost("project.estimateCost"),
    startDate("project.startDate"),
    endDate("project.endDate");

    private final String propertyPath;

    ProjectServiceSortField(String propertyPath) {
        this.propertyPath = propertyPath;
    }

}

