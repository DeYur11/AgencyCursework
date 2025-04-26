package org.example.advertisingagency.dto.common.office;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOfficeInput {
    private String street;
    private Integer cityId;
}