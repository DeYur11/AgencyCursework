package org.example.advertisingagency.dto.material.material;

import org.example.advertisingagency.dto.project.PageInfo;
import org.example.advertisingagency.model.Material;

import java.util.List;

public record MaterialPage(List<Material> content, PageInfo pageInfo) {}