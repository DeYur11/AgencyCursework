package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Integer> {
}