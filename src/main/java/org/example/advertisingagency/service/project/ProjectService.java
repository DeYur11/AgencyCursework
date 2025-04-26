package org.example.advertisingagency.service.project;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.project.CreateProjectInput;
import org.example.advertisingagency.dto.project.UpdateProjectInput;
import org.example.advertisingagency.model.*;
import org.example.advertisingagency.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectStatusRepository projectStatusRepository;
    private final ProjectTypeRepository projectTypeRepository;
    private final ClientRepository clientRepository;
    private final WorkerRepository workerRepository;
    private final PaymentRepository paymentRepository;
    private final ProjectServiceRepository projectServiceRepository;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectStatusRepository projectStatusRepository,
                          ProjectTypeRepository projectTypeRepository,
                          ClientRepository clientRepository,
                          WorkerRepository workerRepository,
                          PaymentRepository paymentRepository,
                          ProjectServiceRepository projectServiceRepository) {
        this.projectRepository = projectRepository;
        this.projectStatusRepository = projectStatusRepository;
        this.projectTypeRepository = projectTypeRepository;
        this.clientRepository = clientRepository;
        this.workerRepository = workerRepository;
        this.paymentRepository = paymentRepository;
        this.projectServiceRepository = projectServiceRepository;
    }

    public Project getProjectById(Integer id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project createProject(CreateProjectInput input) {
        Project project = new Project();
        project.setName(input.getName());
        project.setRegistrationDate(input.getRegistrationDate());
        project.setStartDate(input.getStartDate());
        project.setEndDate(input.getEndDate());
        project.setCost(input.getCost());
        project.setEstimateCost(input.getEstimateCost());
        if (input.getStatusId() != null) {
            project.setStatus(findStatus(input.getStatusId()));
        }
        project.setProjectType(findType(input.getTypeId()));
        project.setPaymentDeadline(input.getPaymentDeadline());
        project.setClient(findClient(input.getClientId()));
        if (input.getManagerId() != null) {
            project.setManager(findWorker(input.getManagerId()));
        }
        project.setDescription(input.getDescription());
        return projectRepository.save(project);
    }

    public Project updateProject(Integer id, UpdateProjectInput input) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));

        if (input.getName() != null) project.setName(input.getName());
        if (input.getRegistrationDate() != null) project.setRegistrationDate(input.getRegistrationDate());
        if (input.getStartDate() != null) project.setStartDate(input.getStartDate());
        if (input.getEndDate() != null) project.setEndDate(input.getEndDate());
        if (input.getCost() != null) project.setCost(input.getCost());
        if (input.getEstimateCost() != null) project.setEstimateCost(input.getEstimateCost());
        if (input.getStatusId() != null) project.setStatus(findStatus(input.getStatusId()));
        if (input.getTypeId() != null) project.setProjectType(findType(input.getTypeId()));
        if (input.getPaymentDeadline() != null) project.setPaymentDeadline(input.getPaymentDeadline());
        if (input.getClientId() != null) project.setClient(findClient(input.getClientId()));
        if (input.getManagerId() != null) project.setManager(findWorker(input.getManagerId()));
        if (input.getDescription() != null) project.setDescription(input.getDescription());

        return projectRepository.save(project);
    }

    public boolean deleteProject(Integer id) {
        if (!projectRepository.existsById(id)) {
            return false;
        }
        projectRepository.deleteById(id);
        return true;
    }

    private ProjectStatus findStatus(Integer id) {
        return projectStatusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProjectStatus not found with id: " + id));
    }

    private ProjectType findType(Integer id) {
        return projectTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProjectType not found with id: " + id));
    }

    private Client findClient(Integer id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found with id: " + id));
    }

    private Worker findWorker(Integer id) {
        return workerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Worker not found with id: " + id));
    }

    public List<Payment> getPaymentsForProject(Integer projectId) {
        return paymentRepository.findAllByProjectID_Id(projectId);
    }

    public List<org.example.advertisingagency.model.ProjectService> getProjectServicesForProject(Integer projectId) {
        return projectServiceRepository.findAllByProjectID_Id(projectId);
    }
}
