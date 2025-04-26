package org.example.advertisingagency.service.material;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.material.usagerestriction.CreateUsageRestrictionInput;
import org.example.advertisingagency.dto.material.usagerestriction.UpdateUsageRestrictionInput;
import org.example.advertisingagency.model.Material;
import org.example.advertisingagency.model.UsageRestriction;
import org.example.advertisingagency.repository.MaterialRepository;
import org.example.advertisingagency.repository.UsageRestrictionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsageRestrictionService {

    private final UsageRestrictionRepository usageRestrictionRepository;
    private final MaterialRepository materialRepository;

    public UsageRestrictionService(UsageRestrictionRepository usageRestrictionRepository, MaterialRepository materialRepository) {
        this.usageRestrictionRepository = usageRestrictionRepository;
        this.materialRepository = materialRepository;
    }

    public UsageRestriction getUsageRestrictionById(Integer id) {
        return usageRestrictionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("UsageRestriction not found with id: " + id));
    }

    public List<UsageRestriction> getAllUsageRestrictions() {
        return usageRestrictionRepository.findAll();
    }

    public UsageRestriction createUsageRestriction(CreateUsageRestrictionInput input) {
        UsageRestriction usageRestriction = new UsageRestriction();
        usageRestriction.setDescription(input.getDescription());
        return usageRestrictionRepository.save(usageRestriction);
    }

    public UsageRestriction updateUsageRestriction(Integer id, UpdateUsageRestrictionInput input) {
        UsageRestriction usageRestriction = usageRestrictionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("UsageRestriction not found with id: " + id));

        if (input.getDescription() != null) {
            usageRestriction.setDescription(input.getDescription());
        }
        return usageRestrictionRepository.save(usageRestriction);
    }

    public boolean deleteUsageRestriction(Integer id) {
        if (!usageRestrictionRepository.existsById(id)) {
            return false;
        }
        usageRestrictionRepository.deleteById(id);
        return true;
    }

    public List<Material> getMaterialsByUsageRestriction(Integer usageRestrictionId) {
        return materialRepository.findAllByUsageRestriction_Id(usageRestrictionId);
    }
}
