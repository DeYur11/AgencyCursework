package org.example.advertisingagency.service.material;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.material.review.CreateMaterialReviewInput;
import org.example.advertisingagency.dto.material.review.UpdateMaterialReviewInput;
import org.example.advertisingagency.model.Material;
import org.example.advertisingagency.model.MaterialReview;
import org.example.advertisingagency.model.MaterialSummary;
import org.example.advertisingagency.model.Worker;
import org.example.advertisingagency.repository.MaterialRepository;
import org.example.advertisingagency.repository.MaterialReviewRepository;
import org.example.advertisingagency.repository.MaterialSummaryRepository;
import org.example.advertisingagency.repository.WorkerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterialReviewService {

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
        review.setReviewDate(input.getReviewDate());
        if (input.getReviewerId() != null) {
            review.setReviewer(findWorker(input.getReviewerId()));
        }
        return materialReviewRepository.save(review);
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
        return materialReviewRepository.save(review);
    }

    public boolean deleteMaterialReview(Integer id) {
        if (!materialReviewRepository.existsById(id)) {
            return false;
        }
        materialReviewRepository.deleteById(id);
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
}
