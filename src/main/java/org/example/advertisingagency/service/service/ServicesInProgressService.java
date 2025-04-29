package org.example.advertisingagency.service.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.service.servicesinprogress.CreateServicesInProgressInput;
import org.example.advertisingagency.dto.service.servicesinprogress.UpdateServicesInProgressInput;
import org.example.advertisingagency.model.ProjectService;
import org.example.advertisingagency.model.ServiceInProgressStatus;
import org.example.advertisingagency.model.ServicesInProgress;
import org.example.advertisingagency.repository.ProjectServiceRepository;
import org.example.advertisingagency.repository.ServiceInProgressStatusRepository;
import org.example.advertisingagency.repository.ServicesInProgressRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ServicesInProgressService {

    private final ServicesInProgressRepository servicesInProgressRepository;
    private final ServiceInProgressStatusRepository serviceInProgressStatusRepository;
    private final ProjectServiceRepository projectServiceRepository;

    public ServicesInProgressService(ServicesInProgressRepository servicesInProgressRepository,
                                     ServiceInProgressStatusRepository serviceInProgressStatusRepository,
                                     ProjectServiceRepository projectServiceRepository) {
        this.servicesInProgressRepository = servicesInProgressRepository;
        this.serviceInProgressStatusRepository = serviceInProgressStatusRepository;
        this.projectServiceRepository = projectServiceRepository;
    }

    public ServicesInProgress getServicesInProgressById(Integer id) {
        return servicesInProgressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ServicesInProgress not found with id: " + id));
    }

    public List<ServicesInProgress> getAllServicesInProgress() {
        return servicesInProgressRepository.findAll();
    }

    public ServicesInProgress createServicesInProgress(CreateServicesInProgressInput input) {
        ServicesInProgress services = new ServicesInProgress();
        services.setStartDate(input.getStartDate());
        services.setEndDate(input.getEndDate());
        services.setCost(input.getCost() != null ? BigDecimal.valueOf(input.getCost()) : null);
        if (input.getStatusId() != null) {
            services.setStatusID(findStatus(input.getStatusId()));
        }
        if (input.getProjectServiceId() != null) {
            services.setProjectServiceID(findProjectService(input.getProjectServiceId()));
        }
        return servicesInProgressRepository.save(services);
    }

    public ServicesInProgress updateServicesInProgress(Integer id, UpdateServicesInProgressInput input) {
        ServicesInProgress services = servicesInProgressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ServicesInProgress not found with id: " + id));

        if (input.getStartDate() != null) services.setStartDate(input.getStartDate());
        if (input.getEndDate() != null) services.setEndDate(input.getEndDate());
        if (input.getCost() != null) services.setCost(BigDecimal.valueOf(input.getCost()));
        if (input.getStatusId() != null) services.setStatusID(findStatus(input.getStatusId()));
        if (input.getProjectServiceId() != null) services.setProjectServiceID(findProjectService(input.getProjectServiceId()));

        return servicesInProgressRepository.save(services);
    }

    public boolean deleteServicesInProgress(Integer id) {
        if (!servicesInProgressRepository.existsById(id)) {
            return false;
        }
        servicesInProgressRepository.deleteById(id);
        return true;
    }

    private ServiceInProgressStatus findStatus(Integer id) {
        return serviceInProgressStatusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Status not found with id: " + id));
    }

    private ProjectService findProjectService(Integer id) {
        return projectServiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProjectService not found with id: " + id));
    }

    public List<ServicesInProgress> getServicesInProgressByProjectServiceIds(List<Integer> projectServiceIds) {
        return servicesInProgressRepository.findAllByProjectServiceID_IdIn(projectServiceIds);
    }

}
