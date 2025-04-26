package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.service.serviceinprogressstatus.CreateServiceInProgressStatusInput;
import org.example.advertisingagency.dto.service.serviceinprogressstatus.UpdateServiceInProgressStatusInput;
import org.example.advertisingagency.model.ServiceInProgressStatus;
import org.example.advertisingagency.model.ServicesInProgress;
import org.example.advertisingagency.service.service.ServiceInProgressStatusService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
public class ServiceInProgressStatusController {

    private final ServiceInProgressStatusService serviceInProgressStatusService;

    public ServiceInProgressStatusController(ServiceInProgressStatusService serviceInProgressStatusService) {
        this.serviceInProgressStatusService = serviceInProgressStatusService;
    }

    @QueryMapping
    public ServiceInProgressStatus serviceInProgressStatus(@Argument Integer id) {
        return serviceInProgressStatusService.getServiceInProgressStatusById(id);
    }

    @QueryMapping
    public List<ServiceInProgressStatus> serviceInProgressStatuses() {
        return serviceInProgressStatusService.getAllServiceInProgressStatuses();
    }

    @MutationMapping
    @Transactional
    public ServiceInProgressStatus createServiceInProgressStatus(@Argument CreateServiceInProgressStatusInput input) {
        return serviceInProgressStatusService.createServiceInProgressStatus(input);
    }

    @MutationMapping
    @Transactional
    public ServiceInProgressStatus updateServiceInProgressStatus(@Argument Integer id, @Argument UpdateServiceInProgressStatusInput input) {
        return serviceInProgressStatusService.updateServiceInProgressStatus(id, input);
    }

    @MutationMapping
    @Transactional
    public boolean deleteServiceInProgressStatus(@Argument Integer id) {
        return serviceInProgressStatusService.deleteServiceInProgressStatus(id);
    }

    @SchemaMapping(typeName = "ServiceInProgressStatus", field = "servicesInProgress")
    public List<ServicesInProgress> servicesInProgress(ServiceInProgressStatus status) {
        return serviceInProgressStatusService.getServicesInProgressByStatus(status.getId());
    }
}
