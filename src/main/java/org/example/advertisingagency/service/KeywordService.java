package org.example.advertisingagency.service;

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
        keyword.setCreateDatetime(Instant.now());
        keyword.setUpdateDatetime(Instant.now());
        return keywordRepository.save(keyword);
    }

    public Keyword updateKeyword(UpdateKeywordInput input) {
        Keyword keyword = keywordRepository.findById(input.getId())
                .orElseThrow(() -> new RuntimeException("Keyword not found"));
        keyword.setName(input.getName());
        keyword.setUpdateDatetime(Instant.now());
        return keywordRepository.save(keyword);
    }

    public boolean deleteKeyword(Integer id) {
        if (keywordRepository.existsById(id)) {
            keywordRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Keyword getKeywordById(Integer id) {
        return keywordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Keyword not found"));
    }

    public List<Material> getMaterialsForKeyword(Keyword keyword) {
        return materialKeywordRepository.findByKeywordID(keyword)
                .stream()
                .map(MaterialKeyword::getMaterialID)
                .collect(Collectors.toList());
    }


    public List<Keyword> getAllKeywords() {
        return keywordRepository.findAll();
    }
}
