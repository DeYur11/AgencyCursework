package org.example.advertisingagency.dto.payment;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdatePaymentInput {
    private String transactionNumber;
    private Double sum;
    private LocalDate date;
    private Integer projectId;
    private Integer paymentPurposeId;
}
