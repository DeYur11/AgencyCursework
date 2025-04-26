package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.targetaudience.CreateTargetAudienceInput;
import org.example.advertisingagency.dto.targetaudience.UpdateTargetAudienceInput;
import org.example.advertisingagency.model.Material;
import org.example.advertisingagency.model.TargetAudience;
import org.example.advertisingagency.service.material.TargetAudienceService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
public class TargetAudienceController {

    private final TargetAudienceService targetAudienceService;

    public TargetAudienceController(TargetAudienceService targetAudienceService) {
        this.targetAudienceService = targetAudienceService;
    }

    @QueryMapping
    public TargetAudience targetAudience(@Argument Integer id) {
        return targetAudienceService.getTargetAudienceById(id);
    }

    @QueryMapping
    public List<TargetAudience> targetAudiences() {
        return targetAudienceService.getAllTargetAudiences();
    }

    @MutationMapping
    @Transactional
    public TargetAudience createTargetAudience(@Argument CreateTargetAudienceInput input) {
        return targetAudienceService.createTargetAudience(input);
    }

    @MutationMapping
    @Transactional
    public TargetAudience updateTargetAudience(@Argument Integer id, @Argument UpdateTargetAudienceInput input) {
        return targetAudienceService.updateTargetAudience(id, input);
    }

    @MutationMapping
    @Transactional
    public boolean deleteTargetAudience(@Argument Integer id) {
        return targetAudienceService.deleteTargetAudience(id);
    }

    @SchemaMapping(typeName = "TargetAudience", field = "materials")
    public List<Material> materials(TargetAudience targetAudience) {
        return targetAudienceService.getMaterialsByTargetAudience(targetAudience.getId());
    }
}
