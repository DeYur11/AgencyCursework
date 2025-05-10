package org.example.advertisingagency.dto.payment;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdatePaymentInput {
    private String transactionNumber;
    private Double paymentSum;
    private LocalDate paymentDate;
    private Integer projectId;
    private Integer paymentPurposeId;
}
