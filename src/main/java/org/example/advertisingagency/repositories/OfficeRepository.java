package org.example.advertisingagency.repositories;

import org.example.advertisingagency.model.Office;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfficeRepository extends JpaRepository<Office, Integer> {
    List<Office> findOfficesByCity_Id(int cityId);
}