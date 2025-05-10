package org.example.advertisingagency.service.language;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.language.CreateLanguageInput;
import org.example.advertisingagency.dto.language.UpdateLanguageInput;
import org.example.advertisingagency.model.Language;
import org.example.advertisingagency.model.Material;
import org.example.advertisingagency.repository.LanguageRepository;
import org.example.advertisingagency.repository.MaterialRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LanguageService {

    private final LanguageRepository languageRepository;
    private final MaterialRepository materialRepository;

    public LanguageService(LanguageRepository languageRepository, MaterialRepository materialRepository) {
        this.languageRepository = languageRepository;
        this.materialRepository = materialRepository;
    }

    public Language getLanguageById(Integer id) {
        return languageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Language not found with id: " + id));
    }

    public List<Language> getAllLanguages() {
        return languageRepository.findAll();
    }

    public Language createLanguage(CreateLanguageInput input) {
        Language language = new Language();
        language.setName(input.getName());
        return languageRepository.save(language);
    }

    public Language updateLanguage(Integer id, UpdateLanguageInput input) {
        Language language = languageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Language not found with id: " + id));

        if (input.getName() != null) {
            language.setName(input.getName());
        }
        return languageRepository.save(language);
    }

    public boolean deleteLanguage(Integer id) {
        if (!languageRepository.existsById(id)) {
            return false;
        }
        languageRepository.deleteById(id);
        return true;
    }

    public List<Material> getMaterialsByLanguage(Integer languageId) {
        return materialRepository.findAllByLanguage_Id(languageId);
    }
}
