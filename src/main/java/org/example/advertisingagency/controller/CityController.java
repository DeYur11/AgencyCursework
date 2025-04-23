package org.example.advertisingagency.controller;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.city.CreateCityInput;
import org.example.advertisingagency.dto.city.UpdateCityInput;
import org.example.advertisingagency.model.City;
import org.example.advertisingagency.model.Country;
import org.example.advertisingagency.model.Office;
import org.example.advertisingagency.repository.CityRepository;
import org.example.advertisingagency.repository.CountryRepository;
import org.example.advertisingagency.repository.OfficeRepository;
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
public class CityController {

    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final OfficeRepository officeRepository;
    @Autowired
    public CityController(CityRepository cityRepository, CountryRepository countryRepository, OfficeRepository officeRepository) {
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
        this.officeRepository = officeRepository;
    }

    @MutationMapping
    @Transactional
    public City createCity(@Argument CreateCityInput input) {
        Country country = countryRepository.findById(input.getCountryId())
                .orElseThrow(() -> new EntityNotFoundException("Country not found for City creation with countryId: " + input.getCountryId()));

        City city = new City();
        city.setName(input.getName());
        city.setCountry(country);
        return cityRepository.save(city);
    }

    @MutationMapping
    @Transactional
    public City updateCity(@Argument Integer id, @Argument UpdateCityInput input) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("City not found with id: " + id));

        if (input.getName() != null) {
            city.setName(input.getName());
        }
        if (input.getCountryId() != null) {
            Country newCountry = countryRepository.findById(input.getCountryId())
                    .orElseThrow(() -> new EntityNotFoundException("New Country not found for City update with countryId: " + input.getCountryId()));
            city.setCountry(newCountry);
        }
        return cityRepository.save(city);
    }

    @MutationMapping
    @Transactional
    public boolean deleteCity(@Argument Integer id) {
        if (!cityRepository.existsById(id)) {
            return false;
        }

        cityRepository.deleteById(id);
        return true;
    }

    @QueryMapping
    public List<City> cities() {
        return cityRepository.findAll();
    }

    @QueryMapping
    public City city(@Argument Integer id) {
        return cityRepository.findById(id).orElse(null);
    }

    @SchemaMapping(typeName = "City", field = "country")
    public Country getCountryForCity(City city) {
        return city.getCountry();
    }

    @SchemaMapping(typeName = "City", field = "offices")
    public List<Office> getOfficesForCity(City city) {
        if (city.getId() == null) {
            return Collections.emptyList();
        }
        return officeRepository.findOfficesByCity_Id(city.getId());
    }
}