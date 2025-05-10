package org.example.advertisingagency.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileDownloadConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path exportDir = Paths.get("/tmp/exports/"); // або твоя реальна директорія
        String exportPath = exportDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + exportPath + "/");
    }
}
