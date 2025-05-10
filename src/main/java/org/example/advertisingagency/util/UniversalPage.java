package org.example.advertisingagency.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.advertisingagency.dto.project.PageInfo;

import java.util.List;

@Getter
@AllArgsConstructor
public class UniversalPage<T> {
    private List<T> content;
    private PageInfo pageInfo;
}
