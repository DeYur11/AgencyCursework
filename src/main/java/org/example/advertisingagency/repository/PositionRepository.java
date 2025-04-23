package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Integer> {
}