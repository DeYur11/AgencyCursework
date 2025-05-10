package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.language.CreateLanguageInput;
import org.example.advertisingagency.dto.language.UpdateLanguageInput;
import org.example.advertisingagency.model.Language;
import org.example.advertisingagency.model.Material;
import org.example.advertisingagency.service.language.LanguageService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
public class LanguageController {

    private final LanguageService languageService;

    public LanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }

    @QueryMapping
    public Language language(@Argument Integer id) {
        return languageService.getLanguageById(id);
    }

    @QueryMapping
    public List<Language> languages() {
        return languageService.getAllLanguages();
    }

    @MutationMapping
    @Transactional
    public Language createLanguage(@Argument CreateLanguageInput input) {
        return languageService.createLanguage(input);
    }

    @MutationMapping
    @Transactional
    public Language updateLanguage(@Argument Integer id, @Argument UpdateLanguageInput input) {
        return languageService.updateLanguage(id, input);
    }

    @MutationMapping
    @Transactional
    public boolean deleteLanguage(@Argument Integer id) {
        return languageService.deleteLanguage(id);
    }

    @SchemaMapping(typeName = "Language", field = "materials")
    public List<Material> materials(Language language) {
        return languageService.getMaterialsByLanguage(language.getId());
    }
}
