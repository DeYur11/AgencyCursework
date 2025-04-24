package org.example.advertisingagency.dto.office;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOfficeInput {
    private String street;
    private Integer cityId;
}