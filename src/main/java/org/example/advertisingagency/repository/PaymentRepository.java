package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findAllByProjectID_Id(Integer projectId);
    List<Payment> findAllByPaymentPurpose_Id(Integer paymentPurposeId);
    List<Payment> findAllByProjectID_IdIn(List<Integer> projectIds);
}