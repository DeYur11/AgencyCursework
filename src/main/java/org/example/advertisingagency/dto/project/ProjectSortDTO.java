package org.example.advertisingagency.dto.project;

import lombok.Data;

@Data
public class ProjectSortDTO {
    private String field;     // "name", "cost", etc.
    private String direction; // "ASC" / "DESC"
}
