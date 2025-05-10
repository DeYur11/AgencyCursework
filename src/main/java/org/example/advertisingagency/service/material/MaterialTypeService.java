package org.example.advertisingagency.service.material;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.material.type.CreateMaterialTypeInput;
import org.example.advertisingagency.dto.material.type.UpdateMaterialTypeInput;
import org.example.advertisingagency.model.Material;
import org.example.advertisingagency.model.MaterialType;
import org.example.advertisingagency.repository.MaterialRepository;
import org.example.advertisingagency.repository.MaterialTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterialTypeService {

    private final MaterialTypeRepository materialTypeRepository;
    private final MaterialRepository materialRepository;

    public MaterialTypeService(MaterialTypeRepository materialTypeRepository, MaterialRepository materialRepository) {
        this.materialTypeRepository = materialTypeRepository;
        this.materialRepository = materialRepository;
    }

    public MaterialType getMaterialTypeById(Integer id) {
        return materialTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MaterialType not found with id: " + id));
    }

    public List<MaterialType> getAllMaterialTypes() {
        return materialTypeRepository.findAll();
    }

    public MaterialType createMaterialType(CreateMaterialTypeInput input) {
        MaterialType materialType = new MaterialType();
        materialType.setName(input.getName());
        return materialTypeRepository.save(materialType);
    }

    public MaterialType updateMaterialType(Integer id, UpdateMaterialTypeInput input) {
        MaterialType materialType = materialTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MaterialType not found with id: " + id));

        if (input.getName() != null) {
            materialType.setName(input.getName());
        }
        return materialTypeRepository.save(materialType);
    }

    public boolean deleteMaterialType(Integer id) {
        if (!materialTypeRepository.existsById(id)) {
            return false;
        }
        materialTypeRepository.deleteById(id);
        return true;
    }

    public List<Material> getMaterialsByMaterialType(Integer typeId) {
        return materialRepository.findAllByType_Id(typeId);
    }
}
