package org.example.advertisingagency.controller;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.common.office.CreateOfficeInput;
import org.example.advertisingagency.dto.common.office.UpdateOfficeInput;
import org.example.advertisingagency.model.City;
import org.example.advertisingagency.model.Office;
import org.example.advertisingagency.model.Worker;
import org.example.advertisingagency.repository.CityRepository;
import org.example.advertisingagency.repository.OfficeRepository;
import org.example.advertisingagency.service.user.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
public class OfficeController {

    private final OfficeRepository officeRepository;
    private final CityRepository cityRepository;
    private final WorkerService workerService;

    @Autowired
    public OfficeController(OfficeRepository officeRepository, CityRepository cityRepository, WorkerService workerService) {
        this.officeRepository = officeRepository;
        this.cityRepository = cityRepository;
        this.workerService = workerService;
    }

    @MutationMapping
    @Transactional
    public Office createOffice(@Argument CreateOfficeInput input) {
        City city = cityRepository.findById(input.getCityId())
                .orElseThrow(() -> new EntityNotFoundException("City not found for Office creation with cityId: " + input.getCityId()));

        Office office = new Office();
        office.setStreet(input.getStreet());
        office.setCity(city);
        return officeRepository.save(office);
    }

    @MutationMapping
    @Transactional
    public Office updateOffice(@Argument Integer id, @Argument UpdateOfficeInput input) {
        Office office = officeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Office not found with id: " + id));

        if (input.getStreet() != null) {
            office.setStreet(input.getStreet());
        }
        if (input.getCityId() != null) {
            City newCity = cityRepository.findById(input.getCityId())
                    .orElseThrow(() -> new EntityNotFoundException("New City not found for Office update with cityId: " + input.getCityId()));
            office.setCity(newCity);
        }
        return officeRepository.save(office);
    }

    @MutationMapping
    @Transactional
    public boolean deleteOffice(@Argument Integer id) {
        if (!officeRepository.existsById(id)) {
            return false;
        }

        officeRepository.deleteById(id);
        return true;
    }

    @QueryMapping
    public List<Office> offices() {
        return officeRepository.findAll();
    }

    @QueryMapping
    public Office office(@Argument Integer id) {
        return officeRepository.findById(id).orElse(null);
    }

    @SchemaMapping(typeName = "Office", field = "city")
    public City getCityForOffice(Office office) {
        return office.getCity();
    }

    @SchemaMapping(typeName = "Office", field = "workers")
    public List<Worker> getWorkers(Office office) {
        return workerService.getWorkersByOfficeId(office.getId());
    }
}