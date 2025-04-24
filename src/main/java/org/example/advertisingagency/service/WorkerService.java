package org.example.advertisingagency.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.model.*;
import org.example.advertisingagency.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final MaterialReviewRepository materialReviewRepository;
    private final PositionRepository positionRepository;
    private final OfficeRepository officeRepository;

    @Autowired
    public WorkerService(
            WorkerRepository workerRepository,
            ProjectRepository projectRepository,
            TaskRepository taskRepository,
            MaterialReviewRepository materialReviewRepository,
            PositionRepository positionRepository,
            OfficeRepository officeRepository) {
        this.workerRepository = workerRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.materialReviewRepository = materialReviewRepository;
        this.positionRepository = positionRepository;
        this.officeRepository = officeRepository;
    }

    public List<Worker> getAllWorkers() {
        return workerRepository.findAll();
    }

    public Worker getWorkerById(Integer id) {
        return workerRepository.findById(id).orElse(null);
    }

    @Transactional
    public Worker createWorker(String name, String surname, String email, String phoneNumber,
                               Integer positionId, Integer officeId, Boolean isReviewer) {
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new EntityNotFoundException("Position not found with id: " + positionId));
        Office office = officeRepository.findById(officeId)
                .orElseThrow(() -> new EntityNotFoundException("Office not found with id: " + officeId));

        Worker worker = new Worker();
        worker.setName(name);
        worker.setSurname(surname);
        worker.setEmail(email);
        worker.setPhoneNumber(phoneNumber);
        worker.setIsReviewer(isReviewer);
        worker.setPosition(position);
        worker.setOffice(office);

        return workerRepository.save(worker);
    }

    @Transactional
    public Worker updateWorker(Integer id, String name, String surname, String email, String phoneNumber,
                               Integer positionId, Integer officeId, Boolean isReviewer) {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Worker not found with id: " + id));

        if (name != null) worker.setName(name);
        if (surname != null) worker.setSurname(surname);
        if (email != null) worker.setEmail(email);
        if (phoneNumber != null) worker.setPhoneNumber(phoneNumber);
        if (isReviewer != null) worker.setIsReviewer(isReviewer);
        if (positionId != null) {
            Position position = positionRepository.findById(positionId)
                    .orElseThrow(() -> new EntityNotFoundException("Position not found with id: " + positionId));
            worker.setPosition(position);
        }
        if (officeId != null) {
            Office office = officeRepository.findById(officeId)
                    .orElseThrow(() -> new EntityNotFoundException("Office not found with id: " + officeId));
            worker.setOffice(office);
        }

        return workerRepository.save(worker);
    }

    @Transactional
    public boolean deleteWorker(Integer id) {
        if (!workerRepository.existsById(id)) return false;
        workerRepository.deleteById(id);
        return true;
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
}
