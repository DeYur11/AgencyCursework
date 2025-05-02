package org.example.advertisingagency.service.project;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import org.example.advertisingagency.dto.project.CreateProjectInput;
import org.example.advertisingagency.dto.project.ProjectFilterDTO;
import org.example.advertisingagency.dto.project.ProjectSortDTO;
import org.example.advertisingagency.dto.project.UpdateProjectInput;
import org.example.advertisingagency.model.*;
import org.example.advertisingagency.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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

    public List<Project> getProjectsByIds(List<Integer> ids) {
        return projectRepository.findAllById(ids);
    }

    public Project createProject(CreateProjectInput input) {
        Project project = new Project();
        project.setName(input.getName());
        project.setDescription(input.getDescription());
        project.setCost(input.getCost() != null ? BigDecimal.valueOf(input.getCost()) : BigDecimal.ZERO);
        project.setEstimateCost(input.getEstimateCost() != null ? BigDecimal.valueOf(input.getEstimateCost()) : BigDecimal.ZERO);
        project.setPaymentDeadline(LocalDate.parse(input.getPaymentDeadline()));
        project.setRegistrationDate(LocalDate.now());  // Тут можна встановити поточну дату як реєстраційну

        // Пошук клієнта, типу проекту та менеджера (якщо вони є)
        Client client = clientRepository.findById(input.getClientId()).orElseThrow(() -> new RuntimeException("Client not found"));
        ProjectType projectType = projectTypeRepository.findById(input.getProjectTypeId()).orElseThrow(() -> new RuntimeException("ProjectType not found"));
        Worker manager = input.getManagerId() != null ? workerRepository.findById(input.getManagerId()).orElse(null) : null;
        project.setStatus(projectStatusRepository.findByName("Not Started").orElse(null));
        project.setClient(client);
        project.setProjectType(projectType);
        project.setManager(manager);

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

    public List<Project> getProjectsByClient(Integer clientId) {
        return projectRepository.findAllByClient_Id(clientId);
    }

    public Page<Project> getPaginatedProjects(
            int page, int size,
            ProjectFilterDTO filter,
            List<ProjectSortDTO> sort
    ) {
        Pageable pageable = PageRequest.of(page, size, buildSort(sort));
        Specification<Project> spec = buildSpecification(filter);
        return projectRepository.findAll(spec, pageable);
    }

    private Sort buildSort(List<ProjectSortDTO> sortDTOs) {
        if (sortDTOs == null || sortDTOs.isEmpty()) return Sort.unsorted();
        List<Sort.Order> orders = sortDTOs.stream()
                .map(dto -> new Sort.Order(
                        Sort.Direction.fromString(dto.getDirection()),
                        mapSortField(dto.getField())
                ))
                .toList();
        return Sort.by(orders);
    }

    private String mapSortField(String field) {
        return switch (field) {
            case "NAME" -> "name";
            case "startDate" -> "startDate";
            case "COST" -> "cost";
            default -> "id";
        };
    }

    private Specification<Project> buildSpecification(ProjectFilterDTO filter) {
        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (filter.getNameContains() != null)
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getNameContains().toLowerCase() + "%"));

            if (filter.getStatusId() != null)
                predicates.add(cb.equal(root.get("status").get("id"), filter.getStatusId()));

            if (filter.getClientId() != null)
                predicates.add(cb.equal(root.get("client").get("id"), filter.getClientId()));

            if (filter.getMinCost() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("cost"), filter.getMinCost()));

            if (filter.getMaxCost() != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("cost"), filter.getMaxCost()));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
