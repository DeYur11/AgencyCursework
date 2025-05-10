package org.example.advertisingagency.service.material;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.material.keyword.CreateKeywordInput;
import org.example.advertisingagency.dto.material.keyword.UpdateKeywordInput;
import org.example.advertisingagency.model.Keyword;
import org.example.advertisingagency.model.Material;
import org.example.advertisingagency.model.MaterialKeyword;
import org.example.advertisingagency.repository.KeywordRepository;
import org.example.advertisingagency.repository.MaterialKeywordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KeywordService {

    private final KeywordRepository keywordRepository;
    private final MaterialKeywordRepository materialKeywordRepository;

    @Autowired
    public KeywordService(KeywordRepository keywordRepository, MaterialKeywordRepository materialKeywordRepository) {
        this.keywordRepository = keywordRepository;
        this.materialKeywordRepository = materialKeywordRepository;
    }

    public Keyword createKeyword(CreateKeywordInput input) {
        Keyword keyword = new Keyword();
        keyword.setName(input.getName());
        return keywordRepository.save(keyword);
    }

    public Keyword updateKeyword(Integer id, UpdateKeywordInput input) {
        Keyword keyword = keywordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Keyword not found with id: " + id));

        if (input.getName() != null) {
            keyword.setName(input.getName());
        }
        return keywordRepository.save(keyword);
    }

    public Boolean deleteKeyword(Integer id) {
        if (!keywordRepository.existsById(id)) {
            return false;
        }
        keywordRepository.deleteById(id);
        return true;
    }

    public Keyword getKeywordById(Integer id) {
        return keywordRepository.findById(id).orElse(null);
    }

    public List<Keyword> getAllKeywords() {
        return keywordRepository.findAll();
    }

    public List<Material> getMaterialsForKeyword(Keyword keyword) {
        return materialKeywordRepository.findByKeyword(keyword)
                .stream()
                .map(MaterialKeyword::getMaterial)
                .collect(Collectors.toList());
    }
}
