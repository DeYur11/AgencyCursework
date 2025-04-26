package org.example.advertisingagency.service.material;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.material.summary.CreateMaterialSummaryInput;
import org.example.advertisingagency.dto.material.summary.UpdateMaterialSummaryInput;
import org.example.advertisingagency.model.MaterialSummary;
import org.example.advertisingagency.model.MaterialReview;
import org.example.advertisingagency.repository.MaterialSummaryRepository;
import org.example.advertisingagency.repository.MaterialReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterialSummaryService {

    private final MaterialSummaryRepository materialSummaryRepository;
    private final MaterialReviewRepository materialReviewRepository;

    public MaterialSummaryService(MaterialSummaryRepository materialSummaryRepository,
                                  MaterialReviewRepository materialReviewRepository) {
        this.materialSummaryRepository = materialSummaryRepository;
        this.materialReviewRepository = materialReviewRepository;
    }

    public MaterialSummary getMaterialSummaryById(Integer id) {
        return materialSummaryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MaterialSummary not found with id: " + id));
    }

    public List<MaterialSummary> getAllMaterialSummaries() {
        return materialSummaryRepository.findAll();
    }

    public MaterialSummary createMaterialSummary(CreateMaterialSummaryInput input) {
        MaterialSummary summary = new MaterialSummary();
        summary.setName(input.getName());
        return materialSummaryRepository.save(summary);
    }

    public MaterialSummary updateMaterialSummary(Integer id, UpdateMaterialSummaryInput input) {
        MaterialSummary summary = materialSummaryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MaterialSummary not found with id: " + id));

        if (input.getName() != null) {
            summary.setName(input.getName());
        }
        return materialSummaryRepository.save(summary);
    }

    public boolean deleteMaterialSummary(Integer id) {
        if (!materialSummaryRepository.existsById(id)) {
            return false;
        }
        materialSummaryRepository.deleteById(id);
        return true;
    }

    public List<MaterialReview> getMaterialReviewsBySummary(Integer summaryId) {
        return materialReviewRepository.findAllByMaterialSummary_Id(summaryId);
    }
}
