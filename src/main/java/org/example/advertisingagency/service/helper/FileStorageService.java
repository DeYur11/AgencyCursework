package org.example.advertisingagency.service.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.*;

@Service
public class FileStorageService {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
    private final String exportPath = "/tmp/exports/";

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public String saveAndGetDownloadUrl(String filename, String content) {
        try {
            Path path = Paths.get(exportPath + filename);
            Files.createDirectories(path.getParent());
            Files.writeString(path, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            logger.info("Exported file saved to directory: {}", path.getParent().toAbsolutePath());

            // Schedule deletion in 15 minutes
            scheduler.schedule(() -> {
                try {
                    Files.deleteIfExists(path);
                    logger.info("File deleted after 15 minutes: {}", path.toAbsolutePath());
                } catch (IOException e) {
                    logger.warn("Failed to delete file after delay: {}", path.toAbsolutePath(), e);
                }
            }, 15, TimeUnit.MINUTES);

            return "/files/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to write export file", e);
        }
    }
}
