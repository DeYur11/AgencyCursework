package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.service.servicesinprogress.CreateServicesInProgressInput;
import org.example.advertisingagency.dto.service.servicesinprogress.UpdateServicesInProgressInput;
import org.example.advertisingagency.model.ServicesInProgress;
import org.example.advertisingagency.service.service.ServicesInProgressService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
public class ServicesInProgressController {

    private final ServicesInProgressService servicesInProgressService;

    public ServicesInProgressController(ServicesInProgressService servicesInProgressService) {
        this.servicesInProgressService = servicesInProgressService;
    }

    @QueryMapping
    public ServicesInProgress serviceInProgress(@Argument Integer id) {
        return servicesInProgressService.getServicesInProgressById(id);
    }

    @QueryMapping
    public List<ServicesInProgress> servicesInProgress() {
        return servicesInProgressService.getAllServicesInProgress();
    }

    @MutationMapping
    @Transactional
    public ServicesInProgress createServiceInProgress(@Argument CreateServicesInProgressInput input) {
        return servicesInProgressService.createServicesInProgress(input);
    }

    @MutationMapping
    @Transactional
    public ServicesInProgress updateServiceInProgress(@Argument Integer id, @Argument UpdateServicesInProgressInput input) {
        return servicesInProgressService.updateServicesInProgress(id, input);
    }

    @MutationMapping
    @Transactional
    public boolean deleteServiceInProgress(@Argument Integer id) {
        return servicesInProgressService.deleteServicesInProgress(id);
    }
}
