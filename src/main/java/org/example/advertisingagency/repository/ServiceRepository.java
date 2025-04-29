package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Integer> {
    List<Service> findAllByServiceType_Id(Integer serviceTypeId);
}