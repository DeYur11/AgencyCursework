package org.example.advertisingagency.dto.common.city;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCityInput {
    private String name;
    private Integer countryId;
}