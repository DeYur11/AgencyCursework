package org.example.advertisingagency.dto.auth;

public record WorkerAccountInput(
        Integer workerId,
        String username,
        String password
) {}
