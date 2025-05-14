package org.example.advertisingagency.service.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.service.projectservice.CreateProjectServiceInput;
import org.example.advertisingagency.dto.service.projectservice.UpdateProjectServiceInput;
import org.example.advertisingagency.exception.EntityInUseException;
import org.example.advertisingagency.model.ProjectService;
import org.example.advertisingagency.model.Project;
import org.example.advertisingagency.model.Service;
import org.example.advertisingagency.model.ServicesInProgress;
import org.example.advertisingagency.repository.ProjectRepository;
import org.example.advertisingagency.repository.ProjectServiceRepository;
import org.example.advertisingagency.repository.ServiceRepository;
import org.example.advertisingagency.repository.ServicesInProgressRepository;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

@org.springframework.stereotype.Service
public class ProjectServiceService {

    private final ProjectServiceRepository projectServiceRepository;
    private final ServiceRepository serviceRepository;
    private final ProjectRepository projectRepository;
    private final ServicesInProgressRepository servicesInProgressRepository;

    public ProjectServiceService(ProjectServiceRepository projectServiceRepository,
                                 ServiceRepository serviceRepository,
                                 ProjectRepository projectRepository,
                                 ServicesInProgressRepository servicesInProgressRepository) {
        this.projectServiceRepository = projectServiceRepository;
        this.serviceRepository = serviceRepository;
        this.projectRepository = projectRepository;
        this.servicesInProgressRepository = servicesInProgressRepository;
    }

    public ProjectService getProjectServiceById(Integer id) {
        return projectServiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProjectService not found with id: " + id));
    }

    public List<ProjectService> getAllProjectServices() {
        return projectServiceRepository.findAll();
    }

    public ProjectService createProjectService(CreateProjectServiceInput input) {
        ProjectService projectService = new ProjectService();
        projectService.setService(findService(input.getServiceId()));
        projectService.setProject(findProject(input.getProjectId()));
        projectService.setAmount(input.getAmount());
        return projectServiceRepository.save(projectService);
    }

    public ProjectService updateProjectService(Integer id, UpdateProjectServiceInput input) {
        ProjectService projectService = projectServiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProjectService not found with id: " + id));

        if (input.getServiceId() != null) {
            projectService.setService(findService(input.getServiceId()));
        }
        if (input.getProjectId() != null) {
            projectService.setProject(findProject(input.getProjectId()));
        }
        if (input.getAmount() != null) {
            projectService.setAmount(input.getAmount());
        }

        return projectServiceRepository.save(projectService);
    }

    public boolean deleteProjectService(Integer id) {
        if (!projectServiceRepository.existsById(id)) {
            return false;
        }
        try {
            projectServiceRepository.deleteById(id);
            projectServiceRepository.flush();
        }catch (DataIntegrityViolationException e) {
            throw new EntityInUseException("Замовлення має реалізації");
        }

        return true;
    }

    private Service findService(Integer serviceId) {
        return serviceRepository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("Service not found with id: " + serviceId));
    }

    private Project findProject(Integer projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + projectId));
    }

    public List<ServicesInProgress> getServicesInProgressByProjectService(Integer projectServiceId) {
        return servicesInProgressRepository.findAllByProjectService_Id(projectServiceId);
    }

    public List<ProjectService> getProjectServicesByProject(Integer projectId) {
        return projectServiceRepository.findAllByProject_Id(projectId);
    }
}
