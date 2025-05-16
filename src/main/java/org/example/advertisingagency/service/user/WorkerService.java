package org.example.advertisingagency.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.worker.CreateWorkerInput;
import org.example.advertisingagency.dto.worker.UpdateWorkerInput;
import org.example.advertisingagency.exception.EntityInUseException;
import org.example.advertisingagency.model.*;
import org.example.advertisingagency.model.log.AuditAction;
import org.example.advertisingagency.model.log.AuditEntity;
import org.example.advertisingagency.repository.*;
import org.example.advertisingagency.service.logs.TransactionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final MaterialReviewRepository materialReviewRepository;
    private final PositionRepository positionRepository;
    private final OfficeRepository officeRepository;
    private final TransactionLogService transactionLogService;

    @Autowired
    public WorkerService(
            WorkerRepository workerRepository,
            ProjectRepository projectRepository,
            TaskRepository taskRepository,
            MaterialReviewRepository materialReviewRepository,
            PositionRepository positionRepository,
            OfficeRepository officeRepository,
            TransactionLogService transactionLogService) {
        this.workerRepository = workerRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.materialReviewRepository = materialReviewRepository;
        this.positionRepository = positionRepository;
        this.officeRepository = officeRepository;
        this.transactionLogService = transactionLogService;
    }

    public List<Worker> getAllWorkers() {
        return workerRepository.findAll();
    }

    public Worker getWorkerById(Integer id) {
        return workerRepository.findById(id).orElse(null);
    }

    public Optional<Worker> getOptionalWorker(Integer id) {
        return workerRepository.findById(id);
    }

    @Transactional
    public Worker createWorker(CreateWorkerInput input) {
        // Create new worker with input values
        Worker worker = new Worker();
        worker.setName(input.getName());
        worker.setSurname(input.getSurname());
        worker.setEmail(input.getEmail());
        worker.setPhoneNumber(input.getPhoneNumber());
        worker.setIsReviewer(input.getIsReviewer());

        // Set position and office
        worker.setPosition(positionRepository.findById(input.getPositionId()).orElseThrow());
        worker.setOffice(officeRepository.findById(input.getOfficeId()).orElseThrow());

        // Save worker
        Worker savedWorker = workerRepository.save(worker);

        // Log the transaction
        Map<String, Integer> relatedIds = getRelatedIds(savedWorker);
        transactionLogService.logTransaction(
                AuditEntity.WORKER,
                savedWorker.getId(),
                AuditAction.CREATE,
                null, // No previous state for creation
                savedWorker,
                "Worker created: " + savedWorker.getName() + " " + savedWorker.getSurname(),
                relatedIds
        );

        return savedWorker;
    }

    @Transactional
    public Worker updateWorker(Integer id, UpdateWorkerInput input) {
        // Find worker to update
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Worker not found with id: " + id));

        // Store previous state for potential rollback
        Worker previousState = cloneWorker(worker);

        // Update worker fields
        if (input.getName() != null) worker.setName(input.getName());
        if (input.getSurname() != null) worker.setSurname(input.getSurname());
        if (input.getEmail() != null) worker.setEmail(input.getEmail());
        if (input.getPhoneNumber() != null) worker.setPhoneNumber(input.getPhoneNumber());
        if (input.getIsReviewer() != null) worker.setIsReviewer(input.getIsReviewer());

        if (input.getPositionId() != null) {
            worker.setPosition(positionRepository.findById(input.getPositionId()).orElseThrow());
        }

        if (input.getOfficeId() != null) {
            worker.setOffice(officeRepository.findById(input.getOfficeId()).orElseThrow());
        }

        // Save updated worker
        Worker updatedWorker = workerRepository.save(worker);

        // Log the transaction
        Map<String, Integer> relatedIds = getRelatedIds(updatedWorker);
        transactionLogService.logTransaction(
                AuditEntity.WORKER,
                updatedWorker.getId(),
                AuditAction.UPDATE,
                previousState,
                updatedWorker,
                "Worker updated: " + updatedWorker.getName() + " " + updatedWorker.getSurname(),
                relatedIds
        );

        return updatedWorker;
    }

    @Transactional
    public boolean deleteWorker(Integer id) {
        // Check if worker exists
        if (!workerRepository.existsById(id)) {
            return false;
        }

        // Get worker before deletion for transaction log
        Worker worker = workerRepository.findById(id).orElse(null);
        if (worker == null) {
            return false;
        }

        // Store previous state for rollback
        Worker previousState = cloneWorker(worker);
        Map<String, Integer> relatedIds = getRelatedIds(worker);

        try {
            workerRepository.deleteById(id);
            workerRepository.flush();

            // Log the transaction
            transactionLogService.logTransaction(
                    AuditEntity.WORKER,
                    id,
                    AuditAction.DELETE,
                    previousState,
                    null, // No current state after deletion
                    "Worker deleted: " + worker.getName() + " " + worker.getSurname(),
                    relatedIds
            );

            return true;
        } catch (DataIntegrityViolationException ex) {
            throw new EntityInUseException("Worker has related records and cannot be deleted");
        }
    }

    public List<Project> getManagedProjects(Integer workerId) {
        return projectRepository.findAllByManager_Id(workerId);
    }

    public List<Task> getAssignedTasks(Integer workerId) {
        return taskRepository.findAllByAssignedWorker_Id(workerId);
    }

    public List<MaterialReview> getMaterialReviews(Integer workerId) {
        return materialReviewRepository.findAllByReviewer_Id(workerId);
    }

    public List<Worker> getWorkersByOfficeId(Integer officeId) {
        return workerRepository.findAllByOffice_Id(officeId);
    }

    // Helper method to clone a worker for rollback
    private Worker cloneWorker(Worker original) {
        Worker clone = new Worker();
        clone.setId(original.getId());
        clone.setName(original.getName());
        clone.setSurname(original.getSurname());
        clone.setEmail(original.getEmail());
        clone.setPhoneNumber(original.getPhoneNumber());
        clone.setIsReviewer(original.getIsReviewer());
        clone.setPosition(original.getPosition());
        clone.setOffice(original.getOffice());
        clone.setCreateDatetime(original.getCreateDatetime());
        clone.setUpdateDatetime(original.getUpdateDatetime());
        return clone;
    }

    // Helper method to get related entity IDs for logging
    private Map<String, Integer> getRelatedIds(Worker worker) {
        Map<String, Integer> ids = new HashMap<>();
        ids.put("workerId", worker.getId());

        if (worker.getPosition() != null) {
            ids.put("positionId", worker.getPosition().getId());
        }

        if (worker.getOffice() != null) {
            ids.put("officeId", worker.getOffice().getId());

            if (worker.getOffice().getCity() != null) {
                ids.put("cityId", worker.getOffice().getCity().getId());
            }
        }

        return ids;
    }
}