package org.example.advertisingagency.dto.office;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOfficeInput {
    private String street;
    private Integer cityId;
}