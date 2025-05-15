package org.example.advertisingagency.dto.rollback;

import lombok.Getter;

/**
 * Input for rollback requests.
 */
@Getter
public record RollbackInput(
        String transactionId
) {
}