package org.example.advertisingagency.service.material;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.material.review.CreateMaterialReviewInput;
import org.example.advertisingagency.dto.material.review.UpdateMaterialReviewInput;
import org.example.advertisingagency.event.AuditLogEvent;
import org.example.advertisingagency.model.Material;
import org.example.advertisingagency.model.MaterialReview;
import org.example.advertisingagency.model.MaterialSummary;
import org.example.advertisingagency.model.Worker;
import org.example.advertisingagency.model.log.AuditAction;
import org.example.advertisingagency.model.log.AuditEntity;
import org.example.advertisingagency.model.log.AuditLog;
import org.example.advertisingagency.repository.MaterialRepository;
import org.example.advertisingagency.repository.MaterialReviewRepository;
import org.example.advertisingagency.repository.MaterialSummaryRepository;
import org.example.advertisingagency.repository.WorkerRepository;
import org.example.advertisingagency.service.auth.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MaterialReviewService {

    private static final Logger log = LoggerFactory.getLogger(MaterialReviewService.class);
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private final MaterialReviewRepository materialReviewRepository;
    private final MaterialRepository materialRepository;
    private final MaterialSummaryRepository materialSummaryRepository;
    private final WorkerRepository workerRepository;
    public MaterialReviewService(MaterialReviewRepository materialReviewRepository,
                                 MaterialRepository materialRepository,
                                 MaterialSummaryRepository materialSummaryRepository,
                                 WorkerRepository workerRepository) {
        this.materialReviewRepository = materialReviewRepository;
        this.materialRepository = materialRepository;
        this.materialSummaryRepository = materialSummaryRepository;
        this.workerRepository = workerRepository;
    }

    public MaterialReview getMaterialReviewById(Integer id) {
        return materialReviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MaterialReview not found with id: " + id));
    }

    public List<MaterialReview> getAllMaterialReviews() {
        return materialReviewRepository.findAll();
    }

    public MaterialReview createMaterialReview(CreateMaterialReviewInput input) {
        MaterialReview review = new MaterialReview();
        review.setMaterial(findMaterial(input.getMaterialId()));
        if (input.getMaterialSummaryId() != null) {
            review.setMaterialSummary(findMaterialSummary(input.getMaterialSummaryId()));
        }
        review.setComments(input.getComments());
        review.setSuggestedChange(input.getSuggestedChange());
        review.setReviewDate(LocalDate.now());
        if (input.getReviewerId() != null) {
            review.setReviewer(findWorker(input.getReviewerId()));
        }

        MaterialReview saved = materialReviewRepository.save(review);

        logAction(AuditAction.CREATE, saved);

        return saved;
    }

    public MaterialReview updateMaterialReview(Integer id, UpdateMaterialReviewInput input) {
        MaterialReview review = materialReviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MaterialReview not found with id: " + id));

        if (input.getMaterialId() != null) {
            review.setMaterial(findMaterial(input.getMaterialId()));
        }
        if (input.getMaterialSummaryId() != null) {
            review.setMaterialSummary(findMaterialSummary(input.getMaterialSummaryId()));
        }
        if (input.getComments() != null) {
            review.setComments(input.getComments());
        }
        if (input.getSuggestedChange() != null) {
            review.setSuggestedChange(input.getSuggestedChange());
        }
        if (input.getReviewDate() != null) {
            review.setReviewDate(input.getReviewDate());
        }
        if (input.getReviewerId() != null) {
            review.setReviewer(findWorker(input.getReviewerId()));
        }

        MaterialReview saved = materialReviewRepository.save(review);

        logAction(AuditAction.UPDATE, saved);
        return saved;
    }

    public boolean deleteMaterialReview(Integer id) {
        Optional<MaterialReview> opt = materialReviewRepository.findById(id);
        if (opt.isEmpty()) {
            return false;
        }

        MaterialReview review = opt.get();
        materialReviewRepository.deleteById(id);

        logAction(AuditAction.DELETE, review);
        return true;
    }

    private Material findMaterial(Integer id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Material not found with id: " + id));
    }

    private MaterialSummary findMaterialSummary(Integer id) {
        return materialSummaryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MaterialSummary not found with id: " + id));
    }

    private Worker findWorker(Integer id) {
        return workerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Worker not found with id: " + id));
    }

    public List<MaterialReview> getReviewsByMaterial(Integer materialId) {
        return Optional.ofNullable(materialReviewRepository.findAllByMaterial_Id(materialId))
                .orElse(List.of());
    }

    private void logAction(AuditAction action, MaterialReview review) {
        var user = UserContextHolder.get();

        AuditLog log = AuditLog.builder()
                .workerId(user.getWorkerId())
                .username(user.getUsername())
                .role(user.getRole())
                .action(action)
                .entity(AuditEntity.MATERIAL_REVIEW)
                .description("MaterialReview " + action + ": " + review.getComments())
                .projectId(review.getMaterial()
                        .getTask()
                        .getServiceInProgress()
                        .getProjectService()
                        .getProject()
                        .getId()
                )
                .serviceInProgressId(review.getMaterial()
                        .getTask()
                        .getServiceInProgress().getId())
                .taskId(review.getMaterial() != null && review.getMaterial().getTask() != null
                        ? review.getMaterial().getTask().getId()
                        : null)
                .materialId(review.getMaterial() != null ? review.getMaterial().getId() : null)
                .materialReviewId(review.getId())
                .timestamp(Instant.now())
                .build();

        MaterialReviewService.log.info("Registered action with review: {} With action: {}", review.getId(), log.getAction());
        eventPublisher.publishEvent(
                new AuditLogEvent(this, log)
        );
    }
}
