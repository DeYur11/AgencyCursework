package org.example.advertisingagency.service.material;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.material.review.CreateMaterialReviewInput;
import org.example.advertisingagency.dto.material.review.UpdateMaterialReviewInput;
import org.example.advertisingagency.model.*;
import org.example.advertisingagency.model.log.AuditAction;
import org.example.advertisingagency.model.log.AuditEntity;
import org.example.advertisingagency.repository.*;
import org.example.advertisingagency.service.logs.TransactionLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service class for MaterialReview that uses the new TransactionLogService
 * instead of the AuditLogService and TransactionLogService.
 * This is an example of how to migrate existing services.
 */
@Service
public class MaterialReviewService {

    private static final Logger log = LoggerFactory.getLogger(MaterialReviewService.class);

    private final MaterialReviewRepository materialReviewRepository;
    private final MaterialRepository materialRepository;
    private final MaterialSummaryRepository materialSummaryRepository;
    private final WorkerRepository workerRepository;
    private final MaterialStatusRepository materialStatusRepository;
    private final TransactionLogService transactionLogService;

    @Autowired
    public MaterialReviewService(
            MaterialReviewRepository materialReviewRepository,
            MaterialRepository materialRepository,
            MaterialSummaryRepository materialSummaryRepository,
            WorkerRepository workerRepository,
            MaterialStatusRepository materialStatusRepository,
            TransactionLogService transactionLogService) {
        this.materialReviewRepository = materialReviewRepository;
        this.materialRepository = materialRepository;
        this.materialSummaryRepository = materialSummaryRepository;
        this.workerRepository = workerRepository;
        this.materialStatusRepository = materialStatusRepository;
        this.transactionLogService = transactionLogService;
    }

    public MaterialReview getMaterialReviewById(Integer id) {
        return materialReviewRepository.findById(id).orElse(null);
    }

    public List<MaterialReview> getAllMaterialReviews() {
        return materialReviewRepository.findAll();
    }

    @Transactional
    public MaterialReview createMaterialReview(CreateMaterialReviewInput input) {
        // Create a new review
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

        // Before making any changes, store the current state of the related material
        Material relatedMaterial = findMaterial(input.getMaterialId());
        Material previousMaterialState = cloneMaterial(relatedMaterial);

        // Save the review
        MaterialReview saved = materialReviewRepository.save(review);

        // Check if material status needs to be updated
        boolean materialStatusUpdated = false;
        long acceptedCount = materialReviewRepository
                .findAllByMaterial_Id(input.getMaterialId())
                .stream()
                .filter(r -> r.getMaterialSummary() != null &&
                        "ACCEPTED".equalsIgnoreCase(r.getMaterialSummary().getName()))
                .count();

        if (acceptedCount >= 3) {
            Material material = saved.getMaterial();
            MaterialStatus acceptedStatus = materialStatusRepository.findByName("Accepted").orElse(null);

            if (acceptedStatus != null && !acceptedStatus.getId().equals(material.getStatus().getId())) {
                material.setStatus(acceptedStatus);
                materialRepository.save(material);
                materialStatusUpdated = true;
            }
        }

        // Log the transaction for the review
        Map<String, Integer> relatedIds = getRelatedIds(saved);
        transactionLogService.logTransaction(
                AuditEntity.MATERIAL_REVIEW,
                saved.getId(),
                AuditAction.CREATE,
                null, // No previous state for creation
                saved,
                "Material review created",
                relatedIds
        );

        // If material status was updated, log that transaction too
        if (materialStatusUpdated) {
            Material updatedMaterial = saved.getMaterial();
            relatedIds.put("materialStatusId", updatedMaterial.getStatus().getId());

            transactionLogService.logTransaction(
                    AuditEntity.MATERIAL,
                    updatedMaterial.getId(),
                    AuditAction.UPDATE,
                    previousMaterialState,
                    updatedMaterial,
                    "Material status updated to Accepted based on reviews",
                    relatedIds
            );
        }

        return saved;
    }

    @Transactional
    public MaterialReview updateMaterialReview(Integer id, UpdateMaterialReviewInput input) {
        // Find the review to update
        MaterialReview review = materialReviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MaterialReview not found with id: " + id));

        // Store previous state for rollback
        MaterialReview previousState = cloneMaterialReview(review);

        // Update review fields
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

        // Save the updated review
        MaterialReview saved = materialReviewRepository.save(review);

        // Log the transaction
        Map<String, Integer> relatedIds = getRelatedIds(saved);
        transactionLogService.logTransaction(
                AuditEntity.MATERIAL_REVIEW,
                saved.getId(),
                AuditAction.UPDATE,
                previousState,
                saved,
                "Material review updated",
                relatedIds
        );

        return saved;
    }

    @Transactional
    public boolean deleteMaterialReview(Integer id) {
        // Check if the review exists
        Optional<MaterialReview> opt = materialReviewRepository.findById(id);
        if (opt.isEmpty()) {
            return false;
        }

        MaterialReview review = opt.get();

        // Store previous state for rollback
        MaterialReview previousState = cloneMaterialReview(review);

        // Get related IDs before deletion
        Map<String, Integer> relatedIds = getRelatedIds(review);

        // Delete the review
        materialReviewRepository.deleteById(id);

        // Log the transaction
        transactionLogService.logTransaction(
                AuditEntity.MATERIAL_REVIEW,
                id,
                AuditAction.DELETE,
                previousState,
                null, // No current state after deletion
                "Material review deleted",
                relatedIds
        );

        return true;
    }

    public List<MaterialReview> getReviewsByMaterial(Integer materialId) {
        return Optional.ofNullable(materialReviewRepository.findAllByMaterial_Id(materialId))
                .orElse(List.of());
    }

    // Helper methods for finding related entities
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

    // Helper method to clone a material review for rollback
    private MaterialReview cloneMaterialReview(MaterialReview original) {
        MaterialReview clone = new MaterialReview();
        clone.setId(original.getId());
        clone.setMaterial(original.getMaterial());
        clone.setMaterialSummary(original.getMaterialSummary());
        clone.setComments(original.getComments());
        clone.setSuggestedChange(original.getSuggestedChange());
        clone.setReviewDate(original.getReviewDate());
        clone.setReviewer(original.getReviewer());
        clone.setCreateDatetime(original.getCreateDatetime());
        clone.setUpdateDatetime(original.getUpdateDatetime());
        return clone;
    }

    // Helper method to clone a material for rollback
    private Material cloneMaterial(Material original) {
        Material clone = new Material();
        clone.setId(original.getId());
        clone.setName(original.getName());
        clone.setDescription(original.getDescription());
        clone.setMaterialType(original.getMaterialType());
        clone.setStatus(original.getStatus());
        clone.setUsageRestriction(original.getUsageRestriction());
        clone.setLicenceType(original.getLicenceType());
        clone.setTargetAudience(original.getTargetAudience());
        clone.setLanguage(original.getLanguage());
        clone.setTask(original.getTask());
        clone.setCreateDatetime(original.getCreateDatetime());
        clone.setUpdateDatetime(original.getUpdateDatetime());
        return clone;
    }

    // Helper method to get related entity IDs for logging
    private Map<String, Integer> getRelatedIds(MaterialReview review) {
        Map<String, Integer> ids = new HashMap<>();
        ids.put("materialReviewId", review.getId());

        if (review.getMaterial() != null) {
            ids.put("materialId", review.getMaterial().getId());

            if (review.getMaterial().getTask() != null) {
                ids.put("taskId", review.getMaterial().getTask().getId());

                if (review.getMaterial().getTask().getServiceInProgress() != null) {
                    ids.put("serviceInProgressId", review.getMaterial().getTask().getServiceInProgress().getId());

                    if (review.getMaterial().getTask().getServiceInProgress().getProjectService() != null &&
                            review.getMaterial().getTask().getServiceInProgress().getProjectService().getProject() != null) {
                        ids.put("projectId", review.getMaterial().getTask().getServiceInProgress().getProjectService().getProject().getId());
                    }
                }
            }
        }

        if (review.getReviewer() != null) {
            ids.put("workerId", review.getReviewer().getId());
        }

        if (review.getMaterialSummary() != null) {
            ids.put("materialSummaryId", review.getMaterialSummary().getId());
        }

        return ids;
    }
}