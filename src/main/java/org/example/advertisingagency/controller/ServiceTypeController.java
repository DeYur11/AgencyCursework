package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.service.type.CreateServiceTypeInput;
import org.example.advertisingagency.dto.service.type.UpdateServiceTypeInput;
import org.example.advertisingagency.model.ServiceType;
import org.example.advertisingagency.model.Service;
import org.example.advertisingagency.service.service.ServiceTypeService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
public class ServiceTypeController {

    private final ServiceTypeService serviceTypeService;

    public ServiceTypeController(ServiceTypeService serviceTypeService) {
        this.serviceTypeService = serviceTypeService;
    }

    @QueryMapping
    public ServiceType serviceType(@Argument Integer id) {
        return serviceTypeService.getServiceTypeById(id);
    }

    @QueryMapping
    public List<ServiceType> serviceTypes() {
        return serviceTypeService.getAllServiceTypes();
    }

    @MutationMapping
    @Transactional
    public ServiceType createServiceType(@Argument CreateServiceTypeInput input) {
        return serviceTypeService.createServiceType(input);
    }

    @MutationMapping
    @Transactional
    public ServiceType updateServiceType(@Argument Integer id, @Argument UpdateServiceTypeInput input) {
        return serviceTypeService.updateServiceType(id, input);
    }

    @MutationMapping
    @Transactional
    public boolean deleteServiceType(@Argument Integer id) {
        return serviceTypeService.deleteServiceType(id);
    }

    @SchemaMapping(typeName = "ServiceType", field = "services")
    public List<Service> services(ServiceType serviceType) {
        return serviceTypeService.getServicesByServiceType(serviceType.getId());
    }
}
