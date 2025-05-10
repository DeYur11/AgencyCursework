package org.example.advertisingagency.service.material;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.licencetype.CreateLicenceTypeInput;
import org.example.advertisingagency.dto.licencetype.UpdateLicenceTypeInput;
import org.example.advertisingagency.model.LicenceType;
import org.example.advertisingagency.model.Material;
import org.example.advertisingagency.repository.LicenceTypeRepository;
import org.example.advertisingagency.repository.MaterialRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LicenceTypeService {

    private final LicenceTypeRepository licenceTypeRepository;
    private final MaterialRepository materialRepository;

    public LicenceTypeService(LicenceTypeRepository licenceTypeRepository, MaterialRepository materialRepository) {
        this.licenceTypeRepository = licenceTypeRepository;
        this.materialRepository = materialRepository;
    }

    public LicenceType getLicenceTypeById(Integer id) {
        return licenceTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("LicenceType not found with id: " + id));
    }

    public List<LicenceType> getAllLicenceTypes() {
        return licenceTypeRepository.findAll();
    }

    public LicenceType createLicenceType(CreateLicenceTypeInput input) {
        LicenceType licenceType = new LicenceType();
        licenceType.setName(input.getName());
        return licenceTypeRepository.save(licenceType);
    }

    public LicenceType updateLicenceType(Integer id, UpdateLicenceTypeInput input) {
        LicenceType licenceType = licenceTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("LicenceType not found with id: " + id));

        if (input.getName() != null) {
            licenceType.setName(input.getName());
        }
        return licenceTypeRepository.save(licenceType);
    }

    public boolean deleteLicenceType(Integer id) {
        if (!licenceTypeRepository.existsById(id)) {
            return false;
        }
        licenceTypeRepository.deleteById(id);
        return true;
    }

    public List<Material> getMaterialsByLicenceType(Integer licenceTypeId) {
        return materialRepository.findAllByLicenceType_Id(licenceTypeId);
    }
}
