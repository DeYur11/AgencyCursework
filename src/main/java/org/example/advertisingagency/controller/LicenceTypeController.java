package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.licencetype.CreateLicenceTypeInput;
import org.example.advertisingagency.dto.licencetype.UpdateLicenceTypeInput;
import org.example.advertisingagency.model.LicenceType;
import org.example.advertisingagency.model.Material;
import org.example.advertisingagency.service.material.LicenceTypeService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
public class LicenceTypeController {

    private final LicenceTypeService licenceTypeService;

    public LicenceTypeController(LicenceTypeService licenceTypeService) {
        this.licenceTypeService = licenceTypeService;
    }

    @QueryMapping
    public LicenceType licenceType(@Argument Integer id) {
        return licenceTypeService.getLicenceTypeById(id);
    }

    @QueryMapping
    public List<LicenceType> licenceTypes() {
        return licenceTypeService.getAllLicenceTypes();
    }

    @MutationMapping
    @Transactional
    public LicenceType createLicenceType(@Argument CreateLicenceTypeInput input) {
        return licenceTypeService.createLicenceType(input);
    }

    @MutationMapping
    @Transactional
    public LicenceType updateLicenceType(@Argument Integer id, @Argument UpdateLicenceTypeInput input) {
        return licenceTypeService.updateLicenceType(id, input);
    }

    @MutationMapping
    @Transactional
    public boolean deleteLicenceType(@Argument Integer id) {
        return licenceTypeService.deleteLicenceType(id);
    }

    @SchemaMapping(typeName = "LicenceType", field = "materials")
    public List<Material> materials(LicenceType licenceType) {
        return licenceTypeService.getMaterialsByLicenceType(licenceType.getId());
    }
}
