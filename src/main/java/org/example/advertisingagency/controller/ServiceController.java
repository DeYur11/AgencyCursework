package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.service.service.CreateServiceInput;
import org.example.advertisingagency.dto.service.service.UpdateServiceInput;
import org.example.advertisingagency.model.ProjectService;
import org.example.advertisingagency.model.Service;
import org.example.advertisingagency.service.service.ServiceService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @QueryMapping
    public Service service(@Argument Integer id) {
        return serviceService.getServiceById(id);
    }

    @QueryMapping
    public List<Service> services() {
        return serviceService.getAllServices();
    }

    @MutationMapping
    @Transactional
    public Service createService(@Argument CreateServiceInput input) {
        return serviceService.createService(input);
    }

    @MutationMapping
    @Transactional
    public Service updateService(@Argument Integer id, @Argument UpdateServiceInput input) {
        return serviceService.updateService(id, input);
    }

    @MutationMapping
    @Transactional
    public boolean deleteService(@Argument Integer id) {
        return serviceService.deleteService(id);
    }

    @SchemaMapping(typeName = "Service", field = "projectServices")
    public List<ProjectService> projectServices(Service service) {
        return serviceService.getProjectServicesByService(service.getId());
    }
}
