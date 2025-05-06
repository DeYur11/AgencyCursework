package org.example.advertisingagency.dto.auth;

import lombok.Data;

@Data
public class RegisterRequest {
    private Integer workerId;
    private String username;
    private String password;
}
