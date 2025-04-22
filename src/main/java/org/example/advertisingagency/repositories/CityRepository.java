package org.example.advertisingagency.repositories;

import org.example.advertisingagency.model.City;
import org.example.advertisingagency.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Integer> {
    List<City> findCitiesByCountry_Id(int id);
}