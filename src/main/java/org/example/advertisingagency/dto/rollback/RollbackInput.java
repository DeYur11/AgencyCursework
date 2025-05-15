package org.example.advertisingagency.dto.rollback;

import lombok.Getter;

/**
 * Input for rollback requests.
 */
public record RollbackInput(
        String transactionId
) {
}