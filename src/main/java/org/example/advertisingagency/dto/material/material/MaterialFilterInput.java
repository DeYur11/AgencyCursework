package org.example.advertisingagency.dto.material.material;

import java.util.List;

public record MaterialFilterInput(
        String nameContains,
        String descriptionContains,
        List<Integer> statusIds,
        List<Integer> languageIds,
        List<Integer> typeIds,
        List<Integer> taskIds,
        List<Integer> keywordIds,
        List<Integer> usageRestrictionIds,
        List<Integer> licenceTypeIds,
        List<Integer> targetAudienceIds
) {}
