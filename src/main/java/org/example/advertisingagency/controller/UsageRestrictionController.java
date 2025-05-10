package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.material.usagerestriction.CreateUsageRestrictionInput;
import org.example.advertisingagency.dto.material.usagerestriction.UpdateUsageRestrictionInput;
import org.example.advertisingagency.model.Material;
import org.example.advertisingagency.model.UsageRestriction;
import org.example.advertisingagency.service.material.UsageRestrictionService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
public class UsageRestrictionController {

    private final UsageRestrictionService usageRestrictionService;

    public UsageRestrictionController(UsageRestrictionService usageRestrictionService) {
        this.usageRestrictionService = usageRestrictionService;
    }

    @QueryMapping
    public UsageRestriction usageRestriction(@Argument Integer id) {
        return usageRestrictionService.getUsageRestrictionById(id);
    }

    @QueryMapping
    public List<UsageRestriction> usageRestrictions() {
        return usageRestrictionService.getAllUsageRestrictions();
    }

    @MutationMapping
    @Transactional
    public UsageRestriction createUsageRestriction(@Argument CreateUsageRestrictionInput input) {
        return usageRestrictionService.createUsageRestriction(input);
    }

    @MutationMapping
    @Transactional
    public UsageRestriction updateUsageRestriction(@Argument Integer id, @Argument UpdateUsageRestrictionInput input) {
        return usageRestrictionService.updateUsageRestriction(id, input);
    }

    @MutationMapping
    @Transactional
    public boolean deleteUsageRestriction(@Argument Integer id) {
        return usageRestrictionService.deleteUsageRestriction(id);
    }

    @SchemaMapping(typeName = "UsageRestriction", field = "materials")
    public List<Material> materials(UsageRestriction usageRestriction) {
        return usageRestrictionService.getMaterialsByUsageRestriction(usageRestriction.getId());
    }
}
