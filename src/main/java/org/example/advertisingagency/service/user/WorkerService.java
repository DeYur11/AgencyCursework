package org.example.advertisingagency.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.worker.CreateWorkerInput;
import org.example.advertisingagency.dto.worker.UpdateWorkerInput;
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

    public Worker createWorker(CreateWorkerInput input) {
        Worker worker = new Worker();
        worker.setName(input.getName());
        worker.setSurname(input.getSurname());
        worker.setEmail(input.getEmail());
        worker.setPhoneNumber(input.getPhoneNumber());
        worker.setIsReviewer(input.getIsReviewer());

        // Підвантажити Position і Office по input.getPositionId() і input.getOfficeId()
        worker.setPosition(positionRepository.findById(input.getPositionId()).orElseThrow());
        worker.setOffice(officeRepository.findById(input.getOfficeId()).orElseThrow());

        return workerRepository.save(worker);
    }

    public Worker updateWorker(Integer id, UpdateWorkerInput input) {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Worker not found with id: " + id));

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

    public List<Worker> getWorkersByOfficeId(Integer officeId) {
        return workerRepository.findAllByOffice_Id(officeId);
    }
}
