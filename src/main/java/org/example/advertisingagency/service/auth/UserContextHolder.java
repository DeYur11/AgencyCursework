package org.example.advertisingagency.service.auth;

import org.example.advertisingagency.model.auth.AuthenticatedUserContext;

public class UserContextHolder {
    private static final ThreadLocal<AuthenticatedUserContext> context = new ThreadLocal<>();

    public static void set(AuthenticatedUserContext user) {
        context.set(user);
    }

    public static AuthenticatedUserContext get() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}
