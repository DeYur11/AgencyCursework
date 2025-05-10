package org.example.advertisingagency.service.material;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.targetaudience.CreateTargetAudienceInput;
import org.example.advertisingagency.dto.targetaudience.UpdateTargetAudienceInput;
import org.example.advertisingagency.model.Material;
import org.example.advertisingagency.model.TargetAudience;
import org.example.advertisingagency.repository.MaterialRepository;
import org.example.advertisingagency.repository.TargetAudienceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TargetAudienceService {

    private final TargetAudienceRepository targetAudienceRepository;
    private final MaterialRepository materialRepository;

    public TargetAudienceService(TargetAudienceRepository targetAudienceRepository, MaterialRepository materialRepository) {
        this.targetAudienceRepository = targetAudienceRepository;
        this.materialRepository = materialRepository;
    }

    public TargetAudience getTargetAudienceById(Integer id) {
        return targetAudienceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TargetAudience not found with id: " + id));
    }

    public List<TargetAudience> getAllTargetAudiences() {
        return targetAudienceRepository.findAll();
    }

    public TargetAudience createTargetAudience(CreateTargetAudienceInput input) {
        TargetAudience targetAudience = new TargetAudience();
        targetAudience.setName(input.getName());
        return targetAudienceRepository.save(targetAudience);
    }

    public TargetAudience updateTargetAudience(Integer id, UpdateTargetAudienceInput input) {
        TargetAudience targetAudience = targetAudienceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TargetAudience not found with id: " + id));

        if (input.getName() != null) {
            targetAudience.setName(input.getName());
        }
        return targetAudienceRepository.save(targetAudience);
    }

    public boolean deleteTargetAudience(Integer id) {
        if (!targetAudienceRepository.existsById(id)) {
            return false;
        }
        targetAudienceRepository.deleteById(id);
        return true;
    }

    public List<Material> getMaterialsByTargetAudience(Integer targetAudienceId) {
        return materialRepository.findAllByTargetAudience_Id(targetAudienceId);
    }
}
