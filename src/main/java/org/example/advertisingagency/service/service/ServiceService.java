package org.example.advertisingagency.service.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.service.service.CreateServiceInput;
import org.example.advertisingagency.dto.service.service.UpdateServiceInput;
import org.example.advertisingagency.model.ProjectService;
import org.example.advertisingagency.model.Service;
import org.example.advertisingagency.model.ServiceType;
import org.example.advertisingagency.repository.ProjectServiceRepository;
import org.example.advertisingagency.repository.ServiceRepository;
import org.example.advertisingagency.repository.ServiceTypeRepository;

import java.math.BigDecimal;
import java.util.List;

@org.springframework.stereotype.Service
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final ProjectServiceRepository projectServiceRepository;

    public ServiceService(ServiceRepository serviceRepository, ServiceTypeRepository serviceTypeRepository, ProjectServiceRepository projectServiceRepository) {
        this.serviceRepository = serviceRepository;
        this.serviceTypeRepository = serviceTypeRepository;
        this.projectServiceRepository = projectServiceRepository;
    }

    public Service getServiceById(Integer id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service not found with id: " + id));
    }

    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    public Service createService(CreateServiceInput input) {
        Service service = new Service();
        service.setEstimateCost(input.getEstimateCost() != null ? BigDecimal.valueOf(input.getEstimateCost()) : null);
        service.setServiceType(findServiceType(input.getServiceTypeId()));
        service.setServiceName(input.getServiceName());
        return serviceRepository.save(service);
    }

    public Service updateService(Integer id, UpdateServiceInput input) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service not found with id: " + id));

        if (input.getEstimateCost() != null) service.setEstimateCost(BigDecimal.valueOf(input.getEstimateCost()));
        if (input.getServiceTypeId() != null) service.setServiceType(findServiceType(input.getServiceTypeId()));
        if (input.getServiceName() != null) service.setServiceName(input.getServiceName());

        return serviceRepository.save(service);
    }

    public boolean deleteService(Integer id) {
        if (!serviceRepository.existsById(id)) {
            return false;
        }
        serviceRepository.deleteById(id);
        return true;
    }

    private ServiceType findServiceType(Integer id) {
        return serviceTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ServiceType not found with id: " + id));
    }

    public List<ProjectService> getProjectServicesByService(Integer serviceId) {
        return projectServiceRepository.findAllByService_Id(serviceId);
    }

    public List<Service> getServicesByIds(List<Integer> ids) {
        return serviceRepository.findAllById(ids);
    }

}
