package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.payment.CreatePaymentInput;
import org.example.advertisingagency.dto.payment.UpdatePaymentInput;
import org.example.advertisingagency.model.Payment;
import org.example.advertisingagency.model.PaymentPurpose;
import org.example.advertisingagency.model.Project;

import org.example.advertisingagency.service.project.PaymentService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @QueryMapping
    public Payment payment(@Argument Integer id) {
        return paymentService.getPaymentById(id);
    }

    @QueryMapping
    public List<Payment> payments() {
        return paymentService.getAllPayments();
    }

    @MutationMapping
    @Transactional
    public Payment createPayment(@Argument CreatePaymentInput input) {
        return paymentService.createPayment(input);
    }

    @MutationMapping
    @Transactional
    public Payment updatePayment(@Argument Integer id, @Argument UpdatePaymentInput input) {
        return paymentService.updatePayment(id, input);
    }

    @MutationMapping
    @Transactional
    public boolean deletePayment(@Argument Integer id) {
        return paymentService.deletePayment(id);
    }

    @SchemaMapping(typeName = "Payment", field = "project")
    public Project project(Payment payment) {
        return payment.getProjectID();
    }

    @SchemaMapping(typeName = "Payment", field = "paymentPurpose")
    public PaymentPurpose paymentPurpose(Payment payment) {
        return payment.getPaymentPurpose();
    }
}
