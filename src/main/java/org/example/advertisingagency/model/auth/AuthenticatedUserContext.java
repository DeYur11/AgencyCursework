package org.example.advertisingagency.model.auth;

public class AuthenticatedUserContext {
    private final Integer workerId;
    private final String username;
    private final String role;

    public AuthenticatedUserContext(Integer workerId, String username, String role) {
        this.workerId = workerId;
        this.username = username;
        this.role = role;
    }

    public Integer getWorkerId() { return workerId; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
}
