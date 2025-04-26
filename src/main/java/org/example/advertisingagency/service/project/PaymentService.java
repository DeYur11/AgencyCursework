package org.example.advertisingagency.service.project;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.payment.CreatePaymentInput;
import org.example.advertisingagency.dto.payment.UpdatePaymentInput;
import org.example.advertisingagency.model.Payment;
import org.example.advertisingagency.model.PaymentPurpose;
import org.example.advertisingagency.model.Project;
import org.example.advertisingagency.repository.PaymentRepository;
import org.example.advertisingagency.repository.PaymentPurposeRepository;
import org.example.advertisingagency.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ProjectRepository projectRepository;
    private final PaymentPurposeRepository paymentPurposeRepository;

    public PaymentService(PaymentRepository paymentRepository, ProjectRepository projectRepository, PaymentPurposeRepository paymentPurposeRepository) {
        this.paymentRepository = paymentRepository;
        this.projectRepository = projectRepository;
        this.paymentPurposeRepository = paymentPurposeRepository;
    }

    public Payment getPaymentById(Integer id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment createPayment(CreatePaymentInput input) {
        Payment payment = new Payment();
        payment.setTransactionNumber(input.getTransactionNumber());
        payment.setPaymentSum(BigDecimal.valueOf(input.getSum()));
        payment.setPaymentDate(input.getDate());
        payment.setProjectID(findProject(input.getProjectId()));
        if (input.getPaymentPurposeId() != null) {
            payment.setPaymentPurpose(findPaymentPurpose(input.getPaymentPurposeId()));
        }
        return paymentRepository.save(payment);
    }

    public Payment updatePayment(Integer id, UpdatePaymentInput input) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));

        if (input.getTransactionNumber() != null) payment.setTransactionNumber(input.getTransactionNumber());
        if (input.getSum() != null) payment.setPaymentSum(BigDecimal.valueOf(input.getSum()));
        if (input.getDate() != null) payment.setPaymentDate(input.getDate());
        if (input.getProjectId() != null) payment.setProjectID(findProject(input.getProjectId()));
        if (input.getPaymentPurposeId() != null) payment.setPaymentPurpose(findPaymentPurpose(input.getPaymentPurposeId()));

        return paymentRepository.save(payment);
    }

    public boolean deletePayment(Integer id) {
        if (!paymentRepository.existsById(id)) {
            return false;
        }
        paymentRepository.deleteById(id);
        return true;
    }

    public Project findProject(Integer projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + projectId));
    }

    public PaymentPurpose findPaymentPurpose(Integer paymentPurposeId) {
        return paymentPurposeRepository.findById(paymentPurposeId)
                .orElseThrow(() -> new EntityNotFoundException("PaymentPurpose not found with id: " + paymentPurposeId));
    }
}
