package org.example.advertisingagency.service.project;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.StoredProcedureQuery;
import org.example.advertisingagency.dto.PaginatedProjectsInput;
import org.example.advertisingagency.dto.project.CreateProjectInput;
import org.example.advertisingagency.dto.project.ProjectFilterDTO;
import org.example.advertisingagency.dto.project.ProjectSortDTO;
import org.example.advertisingagency.dto.project.UpdateProjectInput;
import org.example.advertisingagency.exception.EntityInUseException;
import org.example.advertisingagency.model.*;
import org.example.advertisingagency.model.log.AuditAction;
import org.example.advertisingagency.model.log.AuditEntity;
import org.example.advertisingagency.repository.*;
import org.example.advertisingagency.service.logs.TransactionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectStatusRepository projectStatusRepository;
    private final ProjectTypeRepository projectTypeRepository;
    private final ClientRepository clientRepository;
    private final WorkerRepository workerRepository;
    private final PaymentRepository paymentRepository;
    private final ProjectServiceRepository projectServiceRepository;
    private final EntityManager entityManager;
    private final TransactionLogService transactionLogService;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectStatusRepository projectStatusRepository,
                          ProjectTypeRepository projectTypeRepository,
                          ClientRepository clientRepository,
                          WorkerRepository workerRepository,
                          PaymentRepository paymentRepository,
                          ProjectServiceRepository projectServiceRepository,
                          EntityManager entityManager,
                          TransactionLogService transactionLogService) {
        this.projectRepository = projectRepository;
        this.projectStatusRepository = projectStatusRepository;
        this.projectTypeRepository = projectTypeRepository;
        this.clientRepository = clientRepository;
        this.workerRepository = workerRepository;
        this.paymentRepository = paymentRepository;
        this.projectServiceRepository = projectServiceRepository;
        this.entityManager = entityManager;
        this.transactionLogService = transactionLogService;
    }

    public Project getProjectById(Integer id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));
    }

    public List<Project> getProjectByProjectManager(Integer projectManagerId) {
        return projectRepository.findAllByManager_Id(projectManagerId);
    }

    @Transactional
    public Project pauseProject(Integer projectId) {
        // Get current state of the project
        Project project = getProjectById(projectId);
        Project previousState = cloneProject(project);

        int pausedStatusId = projectStatusRepository.findByName("Paused").get().getId();
        projectRepository.executeStatusUpdateProcedure(projectId, pausedStatusId);

        // Get updated project after status change
        Project updatedProject = getProjectById(projectId);

        // Log the transaction
        Map<String, Integer> relatedIds = getRelatedIds(updatedProject);
        transactionLogService.logTransaction(
                AuditEntity.PROJECT,
                projectId,
                AuditAction.UPDATE,
                previousState,
                updatedProject,
                "Project paused: " + updatedProject.getName(),
                relatedIds
        );

        return updatedProject;
    }

    @Transactional
    public Project cancelProject(Integer projectId) {
        // Get current state of the project
        Project project = getProjectById(projectId);
        Project previousState = cloneProject(project);

        int cancelledStatusId = projectStatusRepository.findByName("Cancelled").get().getId();
        projectRepository.executeStatusUpdateProcedure(projectId, cancelledStatusId);

        // Get updated project after status change
        Project updatedProject = getProjectById(projectId);

        // Log the transaction
        Map<String, Integer> relatedIds = getRelatedIds(updatedProject);
        transactionLogService.logTransaction(
                AuditEntity.PROJECT,
                projectId,
                AuditAction.UPDATE,
                previousState,
                updatedProject,
                "Project cancelled: " + updatedProject.getName(),
                relatedIds
        );

        return updatedProject;
    }

    @Transactional
    public Project resumeProject(Integer projectId) {
        // Get current state of the project
        Project project = getProjectById(projectId);
        Project previousState = cloneProject(project);

        int inProgressStatusId = projectStatusRepository.findByName("Not Started").get().getId();
        projectRepository.executeStatusUpdateProcedure(projectId, inProgressStatusId);

        // Get updated project after status change
        Project updatedProject = getProjectById(projectId);

        // Log the transaction
        Map<String, Integer> relatedIds = getRelatedIds(updatedProject);
        transactionLogService.logTransaction(
                AuditEntity.PROJECT,
                projectId,
                AuditAction.UPDATE,
                previousState,
                updatedProject,
                "Project resumed: " + updatedProject.getName(),
                relatedIds
        );

        return updatedProject;
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> getProjectsByIds(List<Integer> ids) {
        return projectRepository.findAllById(ids);
    }

    @Transactional
    public Project createProject(CreateProjectInput input) {
        Project project = new Project();
        project.setName(input.getName());
        project.setDescription(input.getDescription());
        project.setCost(input.getCost() != null ? BigDecimal.valueOf(input.getCost()) : BigDecimal.ZERO);
        project.setEstimateCost(input.getEstimateCost() != null ? BigDecimal.valueOf(input.getEstimateCost()) : BigDecimal.ZERO);
        project.setPaymentDeadline(LocalDate.parse(input.getPaymentDeadline()));
        project.setRegistrationDate(LocalDate.now());  // Current date as registration date

        // Find client, project type and manager
        Client client = clientRepository.findById(input.getClientId()).orElseThrow(() -> new RuntimeException("Client not found"));
        ProjectType projectType = projectTypeRepository.findById(input.getProjectTypeId()).orElseThrow(() -> new RuntimeException("ProjectType not found"));
        Worker manager = input.getManagerId() != null ? workerRepository.findById(input.getManagerId()).orElse(null) : null;
        project.setStatus(projectStatusRepository.findByName("Not Started").orElse(null));
        project.setClient(client);
        project.setProjectType(projectType);
        project.setManager(manager);

        // Save the project
        Project savedProject = projectRepository.save(project);

        // Log the transaction
        Map<String, Integer> relatedIds = getRelatedIds(savedProject);
        transactionLogService.logTransaction(
                AuditEntity.PROJECT,
                savedProject.getId(),
                AuditAction.CREATE,
                null, // No previous state for creation
                savedProject,
                "Project created: " + savedProject.getName(),
                relatedIds
        );

        return savedProject;
    }

    @Transactional
    public Project updateStatus(Integer projectId, Integer statusId) {
        // Get current state of the project
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));
        Project previousState = cloneProject(project);

        ProjectStatus newStatus = projectStatusRepository.findById(statusId).orElseThrow(() -> new RuntimeException("Status not found"));

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("usp_UpdateProjectStatusWithCascade");
        query.setParameter("ProjectID", projectId);
        query.setParameter("NewStatusID", statusId);
        query.execute();

        project.setStatus(newStatus);
        Project updatedProject = projectRepository.save(project);

        // Log the transaction
        Map<String, Integer> relatedIds = getRelatedIds(updatedProject);
        transactionLogService.logTransaction(
                AuditEntity.PROJECT,
                projectId,
                AuditAction.UPDATE,
                previousState,
                updatedProject,
                "Project status updated to: " + newStatus.getName(),
                relatedIds
        );

        return updatedProject;
    }

    @Transactional
    public Project updateProject(Integer id, UpdateProjectInput input) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));

        // Store the previous state for rollback
        Project previousState = cloneProject(project);

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

        Project updatedProject = projectRepository.save(project);

        // Log the transaction
        Map<String, Integer> relatedIds = getRelatedIds(updatedProject);
        transactionLogService.logTransaction(
                AuditEntity.PROJECT,
                updatedProject.getId(),
                AuditAction.UPDATE,
                previousState,
                updatedProject,
                "Project updated: " + updatedProject.getName(),
                relatedIds
        );

        return updatedProject;
    }

    @Transactional
    public boolean deleteProject(Integer id) {
        if (!projectRepository.existsById(id)) {
            return false;
        }

        // Get project before deletion for transaction log
        Project project = projectRepository.findById(id).orElse(null);
        if (project == null) {
            return false;
        }

        Project previousState = cloneProject(project);
        Map<String, Integer> relatedIds = getRelatedIds(project);

        try {
            projectRepository.deleteById(id);
            projectRepository.flush();

            // Log the transaction
            transactionLogService.logTransaction(
                    AuditEntity.PROJECT,
                    id,
                    AuditAction.DELETE,
                    previousState,
                    null, // No current state after deletion
                    "Project deleted: " + project.getName(),
                    relatedIds
            );

            return true;
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException("Project has active service orders");
        }
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

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    // Helper method to clone a project for rollback
    private Project cloneProject(Project original) {
        Project clone = new Project();
        clone.setId(original.getId());
        clone.setName(original.getName());
        clone.setDescription(original.getDescription());
        clone.setRegistrationDate(original.getRegistrationDate());
        clone.setStartDate(original.getStartDate());
        clone.setEndDate(original.getEndDate());
        clone.setCost(original.getCost());
        clone.setEstimateCost(original.getEstimateCost());
        clone.setStatus(original.getStatus());
        clone.setProjectType(original.getProjectType());
        clone.setPaymentDeadline(original.getPaymentDeadline());
        clone.setClient(original.getClient());
        clone.setManager(original.getManager());
        clone.setCreateDatetime(original.getCreateDatetime());
        clone.setUpdateDatetime(original.getUpdateDatetime());
        return clone;
    }

    // Helper method to get related entity IDs for logging
    private Map<String, Integer> getRelatedIds(Project project) {
        Map<String, Integer> ids = new HashMap<>();
        ids.put("projectId", project.getId());

        if (project.getClient() != null) {
            ids.put("clientId", project.getClient().getId());
        }

        if (project.getManager() != null) {
            ids.put("managerId", project.getManager().getId());
        }

        if (project.getStatus() != null) {
            ids.put("statusId", project.getStatus().getId());
        }

        return ids;
    }
}