package org.example.advertisingagency.dto.rollback;

/**
 * Response for rollback operations.
 */
public record RollbackResponse(
        boolean success,
        String message,
        String transactionId
) {

}