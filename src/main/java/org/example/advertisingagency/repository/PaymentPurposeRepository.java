package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.PaymentPurpose;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentPurposeRepository extends JpaRepository<PaymentPurpose, Integer> {
}