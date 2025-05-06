package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.WorkerAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkerAccountRepository extends JpaRepository<WorkerAccount, Integer> {
    Optional<WorkerAccount> findByUsernameIgnoreCase(String username);
}