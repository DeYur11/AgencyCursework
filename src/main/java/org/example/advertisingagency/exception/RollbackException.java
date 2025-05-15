package org.example.advertisingagency.exception;

/**
 * Exception thrown when a rollback operation fails.
 */
public class RollbackException extends RuntimeException {
    public RollbackException(String message) {
        super(message);
    }

    public RollbackException(String message, Throwable cause) {
        super(message, cause);
    }
}