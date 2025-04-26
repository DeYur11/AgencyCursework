package org.example.advertisingagency.service.material;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.material.status.CreateMaterialStatusInput;
import org.example.advertisingagency.dto.material.status.UpdateMaterialStatusInput;
import org.example.advertisingagency.model.Material;
import org.example.advertisingagency.model.MaterialStatus;
import org.example.advertisingagency.repository.MaterialRepository;
import org.example.advertisingagency.repository.MaterialStatusRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterialStatusService {

    private final MaterialStatusRepository materialStatusRepository;
    private final MaterialRepository materialRepository;

    public MaterialStatusService(MaterialStatusRepository materialStatusRepository, MaterialRepository materialRepository) {
        this.materialStatusRepository = materialStatusRepository;
        this.materialRepository = materialRepository;
    }

    public MaterialStatus getMaterialStatusById(Integer id) {
        return materialStatusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MaterialStatus not found with id: " + id));
    }

    public List<MaterialStatus> getAllMaterialStatuses() {
        return materialStatusRepository.findAll();
    }

    public MaterialStatus createMaterialStatus(CreateMaterialStatusInput input) {
        MaterialStatus materialStatus = new MaterialStatus();
        materialStatus.setName(input.getName());
        return materialStatusRepository.save(materialStatus);
    }

    public MaterialStatus updateMaterialStatus(Integer id, UpdateMaterialStatusInput input) {
        MaterialStatus materialStatus = materialStatusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MaterialStatus not found with id: " + id));

        if (input.getName() != null) {
            materialStatus.setName(input.getName());
        }
        return materialStatusRepository.save(materialStatus);
    }

    public boolean deleteMaterialStatus(Integer id) {
        if (!materialStatusRepository.existsById(id)) {
            return false;
        }
        materialStatusRepository.deleteById(id);
        return true;
    }

    public List<Material> getMaterialsByMaterialStatus(Integer statusId) {
        return materialRepository.findAllByStatus_Id(statusId);
    }
}
