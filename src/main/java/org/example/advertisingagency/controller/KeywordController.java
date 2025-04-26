package org.example.advertisingagency.controller;


import org.example.advertisingagency.dto.material.keyword.CreateKeywordInput;
import org.example.advertisingagency.dto.material.keyword.UpdateKeywordInput;
import org.example.advertisingagency.model.Keyword;
import org.example.advertisingagency.model.Material;
import org.example.advertisingagency.service.KeywordService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class KeywordController {

    private final KeywordService keywordService;

    public KeywordController(KeywordService keywordService) {
        this.keywordService = keywordService;
    }

    @QueryMapping
    public Keyword keyword(@Argument Integer id) {
        return keywordService.getKeywordById(id);
    }

    @QueryMapping
    public List<Keyword> keywords() {
        return keywordService.getAllKeywords();
    }

    @MutationMapping
    public Keyword createKeyword(@Argument CreateKeywordInput input) {
        return keywordService.createKeyword(input);
    }

    @MutationMapping
    public Keyword updateKeyword(@Argument UpdateKeywordInput input) {
        return keywordService.updateKeyword(input);
    }

    @MutationMapping
    public Boolean deleteKeyword(@Argument Integer id) {
        return keywordService.deleteKeyword(id);
    }

    @SchemaMapping(typeName = "Keyword", field = "materials")
    public List<Material> materials(Keyword keyword) {
        return keywordService.getMaterialsForKeyword(keyword);
    }
}
