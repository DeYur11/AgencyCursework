package org.example.advertisingagency.dto.rollback;

import lombok.Getter;

/**
 * Response for rollback operations.
 */
@Getter
public record RollbackResponse(
        boolean success,
        String message,
        String transactionId
) {

}