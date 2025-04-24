package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Integer> {
}