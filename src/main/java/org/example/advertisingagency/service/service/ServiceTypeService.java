package org.example.advertisingagency.service.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.service.type.CreateServiceTypeInput;
import org.example.advertisingagency.dto.service.type.UpdateServiceTypeInput;
import org.example.advertisingagency.model.ServiceType;
import org.example.advertisingagency.model.Service;
import org.example.advertisingagency.repository.ServiceTypeRepository;
import org.example.advertisingagency.repository.ServiceRepository;


import java.util.List;

@org.springframework.stereotype.Service
public class ServiceTypeService {

    private final ServiceTypeRepository serviceTypeRepository;
    private final ServiceRepository serviceRepository;

    public ServiceTypeService(ServiceTypeRepository serviceTypeRepository, ServiceRepository serviceRepository) {
        this.serviceTypeRepository = serviceTypeRepository;
        this.serviceRepository = serviceRepository;
    }

    public ServiceType getServiceTypeById(Integer id) {
        return serviceTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ServiceType not found with id: " + id));
    }

    public List<ServiceType> getAllServiceTypes() {
        return serviceTypeRepository.findAll();
    }

    public ServiceType createServiceType(CreateServiceTypeInput input) {
        ServiceType serviceType = new ServiceType();
        serviceType.setName(input.getName());
        return serviceTypeRepository.save(serviceType);
    }

    public ServiceType updateServiceType(Integer id, UpdateServiceTypeInput input) {
        ServiceType serviceType = serviceTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ServiceType not found with id: " + id));

        if (input.getName() != null) {
            serviceType.setName(input.getName());
        }
        return serviceTypeRepository.save(serviceType);
    }

    public boolean deleteServiceType(Integer id) {
        if (!serviceTypeRepository.existsById(id)) {
            return false;
        }
        serviceTypeRepository.deleteById(id);
        return true;
    }

    public List<Service> getServicesByServiceType(Integer serviceTypeId) {
        return serviceRepository.findAllByServiceType_Id(serviceTypeId);
    }
}
