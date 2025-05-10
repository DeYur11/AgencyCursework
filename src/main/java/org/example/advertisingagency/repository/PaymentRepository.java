package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findAllByPaymentPurpose_Id(Integer paymentPurposeId);
    List<Payment> findAllByProject_IdIn(List<Integer> projectIds);
    List<Payment> findAllByProject_Id(Integer projectId);
}