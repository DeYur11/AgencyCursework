package org.example.advertisingagency.dto.auth;

public record WorkerAccountDTO(
        Integer id,
        Integer workerId,
        String username,
        String passwordHash
) {}
