package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.country.UpdateCountryInput;
import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.country.CreateCountryInput;
import org.example.advertisingagency.model.City;
import org.example.advertisingagency.model.Country;
import org.example.advertisingagency.repositories.CityRepository;
import org.example.advertisingagency.repositories.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Controller
public class CountryController {

    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;

    @Autowired
    public CountryController(CountryRepository countryRepository, CityRepository cityRepository) {
        this.countryRepository = countryRepository;
        this.cityRepository = cityRepository;
    }

    @MutationMapping
    @Transactional
    public Country createCountry(@Argument CreateCountryInput input) {
        Country country = new Country();
        country.setName(input.getName());
        return countryRepository.save(country);
    }

    @MutationMapping
    @Transactional
    public Country updateCountry(@Argument Integer id, @Argument UpdateCountryInput input) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Country not found with id: " + id));

        if (input.getName() != null) {
            country.setName(input.getName());
        }
        return countryRepository.save(country);
    }

    @MutationMapping
    @Transactional
    public boolean deleteCountry(@Argument Integer id) {
        if (!countryRepository.existsById(id)) {
            return false;
        }
        countryRepository.deleteById(id);
        return true;
    }

    @QueryMapping
    public List<Country> countries() {
        return countryRepository.findAll();
    }

    @QueryMapping
    public Country country(@Argument Integer id) {
        return countryRepository.findById(id).orElse(null);
    }

    @SchemaMapping(typeName = "Country", field = "cities")
    public List<City> getCitiesForCountry(Country country) {
        if (country.getId() == null) {
            return Collections.emptyList();
        }
        return cityRepository.findCitiesByCountry_Id(country.getId());
    }
}