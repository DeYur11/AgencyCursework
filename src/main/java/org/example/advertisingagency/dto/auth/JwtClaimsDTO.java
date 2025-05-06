package org.example.advertisingagency.dto.auth;

public record JwtClaimsDTO(
        Integer workerId,
        String username,
        String name,
        String surname,
        String role,
        boolean isReviewer
) {}

