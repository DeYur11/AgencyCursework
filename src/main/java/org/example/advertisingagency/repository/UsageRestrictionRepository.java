package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.UsageRestriction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsageRestrictionRepository extends JpaRepository<UsageRestriction, Integer> {
}