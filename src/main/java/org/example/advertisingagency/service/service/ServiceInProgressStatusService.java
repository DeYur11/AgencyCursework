package org.example.advertisingagency.service.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.service.serviceinprogressstatus.CreateServiceInProgressStatusInput;
import org.example.advertisingagency.dto.service.serviceinprogressstatus.UpdateServiceInProgressStatusInput;
import org.example.advertisingagency.model.ServiceInProgressStatus;
import org.example.advertisingagency.model.ServicesInProgress;
import org.example.advertisingagency.repository.ServiceInProgressStatusRepository;
import org.example.advertisingagency.repository.ServicesInProgressRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceInProgressStatusService {

    private final ServiceInProgressStatusRepository serviceInProgressStatusRepository;
    private final ServicesInProgressRepository servicesInProgressRepository;

    public ServiceInProgressStatusService(ServiceInProgressStatusRepository serviceInProgressStatusRepository,
                                          ServicesInProgressRepository servicesInProgressRepository) {
        this.serviceInProgressStatusRepository = serviceInProgressStatusRepository;
        this.servicesInProgressRepository = servicesInProgressRepository;
    }

    public ServiceInProgressStatus getServiceInProgressStatusById(Integer id) {
        return serviceInProgressStatusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ServiceInProgressStatus not found with id: " + id));
    }

    public List<ServiceInProgressStatus> getAllServiceInProgressStatuses() {
        return serviceInProgressStatusRepository.findAll();
    }

    public ServiceInProgressStatus createServiceInProgressStatus(CreateServiceInProgressStatusInput input) {
        ServiceInProgressStatus status = new ServiceInProgressStatus();
        status.setName(input.getName());
        return serviceInProgressStatusRepository.save(status);
    }

    public ServiceInProgressStatus updateServiceInProgressStatus(Integer id, UpdateServiceInProgressStatusInput input) {
        ServiceInProgressStatus status = serviceInProgressStatusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ServiceInProgressStatus not found with id: " + id));

        if (input.getName() != null) {
            status.setName(input.getName());
        }
        return serviceInProgressStatusRepository.save(status);
    }

    public boolean deleteServiceInProgressStatus(Integer id) {
        if (!serviceInProgressStatusRepository.existsById(id)) {
            return false;
        }
        serviceInProgressStatusRepository.deleteById(id);
        return true;
    }

    public List<ServicesInProgress> getServicesInProgressByStatus(Integer statusId) {
        return servicesInProgressRepository.findAllByStatusID_Id(statusId);
    }
}
