package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.payment.CreatePaymentPurposeInput;
import org.example.advertisingagency.dto.payment.UpdatePaymentPurposeInput;
import org.example.advertisingagency.model.Payment;
import org.example.advertisingagency.model.PaymentPurpose;
import org.example.advertisingagency.service.project.PaymentPurposeService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
public class PaymentPurposeController {

    private final PaymentPurposeService paymentPurposeService;

    public PaymentPurposeController(PaymentPurposeService paymentPurposeService) {
        this.paymentPurposeService = paymentPurposeService;
    }

    @QueryMapping
    public PaymentPurpose paymentPurpose(@Argument Integer id) {
        return paymentPurposeService.getPaymentPurposeById(id);
    }

    @QueryMapping
    public List<PaymentPurpose> paymentPurposes() {
        return paymentPurposeService.getAllPaymentPurposes();
    }

    @MutationMapping
    @Transactional
    public PaymentPurpose createPaymentPurpose(@Argument CreatePaymentPurposeInput input) {
        return paymentPurposeService.createPaymentPurpose(input);
    }

    @MutationMapping
    @Transactional
    public PaymentPurpose updatePaymentPurpose(@Argument Integer id, @Argument UpdatePaymentPurposeInput input) {
        return paymentPurposeService.updatePaymentPurpose(id, input);
    }

    @MutationMapping
    @Transactional
    public boolean deletePaymentPurpose(@Argument Integer id) {
        return paymentPurposeService.deletePaymentPurpose(id);
    }

    @SchemaMapping(typeName = "PaymentPurpose", field = "payments")
    public List<Payment> payments(PaymentPurpose paymentPurpose) {
        return paymentPurposeService.getPaymentsByPaymentPurpose(paymentPurpose.getId());
    }
}
