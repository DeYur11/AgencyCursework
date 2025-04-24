package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material, Integer> {
}