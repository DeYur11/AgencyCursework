package org.example.advertisingagency.repositories;

import org.example.advertisingagency.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Integer> {
}