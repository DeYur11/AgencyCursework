package org.example.advertisingagency.dto.project;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.advertisingagency.dto.project.PageInfo;
import org.example.advertisingagency.model.Project;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProjectPage {
    private List<Project> content;
    private PageInfo pageInfo;
}
