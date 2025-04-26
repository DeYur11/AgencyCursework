package org.example.advertisingagency.dto.client;

import lombok.Data;

@Data
public class CreateClientInput {
    private String name;
    private String email;
    private String phoneNumber;
}
