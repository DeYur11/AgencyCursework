package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}