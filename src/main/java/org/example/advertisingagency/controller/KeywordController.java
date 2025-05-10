package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.material.keyword.CreateKeywordInput;
import org.example.advertisingagency.dto.material.keyword.UpdateKeywordInput;
import org.example.advertisingagency.model.Keyword;
import org.example.advertisingagency.model.Material;
import org.example.advertisingagency.service.material.KeywordService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
public class KeywordController {

    private final KeywordService keywordService;

    public KeywordController(KeywordService keywordService) {
        this.keywordService = keywordService;
    }

    // ====== QUERY ======

    @QueryMapping
    public Keyword keyword(@Argument Integer id) {
        return keywordService.getKeywordById(id);
    }

    @QueryMapping
    public List<Keyword> keywords() {
        return keywordService.getAllKeywords();
    }

    // ====== MUTATION ======

    @MutationMapping
    @Transactional
    public Keyword createKeyword(@Argument CreateKeywordInput input) {
        return keywordService.createKeyword(input);
    }

    @MutationMapping
    @Transactional
    public Keyword updateKeyword(@Argument Integer id, @Argument UpdateKeywordInput input) {
        return keywordService.updateKeyword(id, input);
    }

    @MutationMapping
    @Transactional
    public Boolean deleteKeyword(@Argument Integer id) {
        return keywordService.deleteKeyword(id);
    }

    // ====== SCHEMA MAPPING ======

    @SchemaMapping(typeName = "Keyword", field = "materials")
    public List<Material> materials(Keyword keyword) {
        return keywordService.getMaterialsForKeyword(keyword);
    }
}
