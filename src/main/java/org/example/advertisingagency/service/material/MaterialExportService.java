package org.example.advertisingagency.service.material;

import org.example.advertisingagency.dto.export.ExportMaterialDTO;
import org.example.advertisingagency.dto.material.material.ExportMaterialsInput;
import org.example.advertisingagency.enums.ExportFormat;
import org.example.advertisingagency.enums.ExportedFile;
import org.example.advertisingagency.mapper.MaterialExportMapper;
import org.example.advertisingagency.model.Material;
import org.example.advertisingagency.repository.MaterialRepository;
import org.example.advertisingagency.service.helper.FileStorageService;
import org.example.advertisingagency.specification.MaterialSpecifications;
import org.example.advertisingagency.util.export.CsvUtil;
import org.example.advertisingagency.util.export.JsonUtil;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterialExportService {

    private final MaterialRepository materialRepository;
    private final FileStorageService fileStorageService;

    public MaterialExportService(MaterialRepository materialRepository,
                                 FileStorageService fileStorageService) {
        this.materialRepository = materialRepository;
        this.fileStorageService = fileStorageService;
    }

    public ExportedFile exportMaterials(ExportMaterialsInput input) {
        Sort sort = (input.sortField() != null && input.sortDirection() != null)
                ? Sort.by(Sort.Direction.valueOf(input.sortDirection().name()), input.sortField().name())
                : Sort.unsorted();

        Specification<Material> spec = MaterialSpecifications.withFilters(input.filter());
        List<Material> materials = materialRepository.findAll(spec, sort);

        // Перетворення на DTO
        List<ExportMaterialDTO> dtos = materials.stream()
                .map(MaterialExportMapper::toDTO)
                .toList();

        // Конвертація
        String content;
        String filename = "materials_" + System.currentTimeMillis();
        if (input.format() == ExportFormat.CSV) {
            content = CsvUtil.convertToCsv(dtos);
            filename += ".csv";
        } else {
            content = JsonUtil.convertToJson(dtos);
            filename += ".json";
        }

        // Запис у файл
        String url = fileStorageService.saveAndGetDownloadUrl(filename, content);
        return new ExportedFile(url, filename, input.format());
    }
}

