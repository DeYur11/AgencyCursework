package org.example.advertisingagency.dto.client;

import lombok.Data;

@Data
public class UpdateClientInput {
    private String name;
    private String email;
    private String phoneNumber;
}
