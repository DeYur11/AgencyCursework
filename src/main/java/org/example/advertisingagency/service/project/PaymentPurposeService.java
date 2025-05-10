package org.example.advertisingagency.service.project;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.payment.CreatePaymentPurposeInput;
import org.example.advertisingagency.dto.payment.UpdatePaymentPurposeInput;
import org.example.advertisingagency.model.PaymentPurpose;
import org.example.advertisingagency.model.Payment;
import org.example.advertisingagency.repository.PaymentPurposeRepository;
import org.example.advertisingagency.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentPurposeService {

    private final PaymentPurposeRepository paymentPurposeRepository;
    private final PaymentRepository paymentRepository;

    public PaymentPurposeService(PaymentPurposeRepository paymentPurposeRepository, PaymentRepository paymentRepository) {
        this.paymentPurposeRepository = paymentPurposeRepository;
        this.paymentRepository = paymentRepository;
    }

    public PaymentPurpose getPaymentPurposeById(Integer id) {
        return paymentPurposeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PaymentPurpose not found with id: " + id));
    }

    public List<PaymentPurpose> getAllPaymentPurposes() {
        return paymentPurposeRepository.findAll();
    }

    public PaymentPurpose createPaymentPurpose(CreatePaymentPurposeInput input) {
        PaymentPurpose paymentPurpose = new PaymentPurpose();
        paymentPurpose.setName(input.getName());
        return paymentPurposeRepository.save(paymentPurpose);
    }

    public PaymentPurpose updatePaymentPurpose(Integer id, UpdatePaymentPurposeInput input) {
        PaymentPurpose paymentPurpose = paymentPurposeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PaymentPurpose not found with id: " + id));

        if (input.getName() != null) {
            paymentPurpose.setName(input.getName());
        }
        return paymentPurposeRepository.save(paymentPurpose);
    }

    public boolean deletePaymentPurpose(Integer id) {
        if (!paymentPurposeRepository.existsById(id)) {
            return false;
        }
        paymentPurposeRepository.deleteById(id);
        return true;
    }

    public List<Payment> getPaymentsByPaymentPurpose(Integer paymentPurposeId) {
        return paymentRepository.findAllByPaymentPurpose_Id(paymentPurposeId);
    }
}
