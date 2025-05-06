package org.example.advertisingagency.service.helper;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
public class FileStorageService {

    private final String exportPath = "/tmp/exports/"; // або шлях до /static/files

    public String saveAndGetDownloadUrl(String filename, String content) {
        try {
            Path path = Paths.get(exportPath + filename);
            Files.createDirectories(path.getParent());
            Files.writeString(path, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            return "/files/" + filename; // URL для frontend
        } catch (IOException e) {
            throw new RuntimeException("Failed to write export file", e);
        }
    }
}

