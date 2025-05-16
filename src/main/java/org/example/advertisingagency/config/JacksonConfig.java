package org.example.advertisingagency.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration for Jackson ObjectMapper with proper handling of
 * Hibernate entities and relationships.
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Handle Java 8 date/time types properly
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        Hibernate5JakartaModule hibernateModule = new Hibernate5JakartaModule();
        // Handle Hibernate entities properly
        // Configure hibernate module to ignore lazy loading
        hibernateModule.configure(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING, false);
        // Important: Let Hibernate handle entity loading rather than forcing it
        hibernateModule.configure(Hibernate5JakartaModule.Feature.REQUIRE_EXPLICIT_LAZY_LOADING_MARKER, false);
        mapper.registerModule(hibernateModule);

        return mapper;
    }

    /**
     * Creates a special ObjectMapper for entity serialization to JSON for logging
     * This mapper is specifically configured for handling entity state for logging
     * and rollbacks.
     */
    @Bean(name = "entityMapper")
    public ObjectMapper entityMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Handle Java 8 date/time types properly
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Ignore Hibernate proxies during serialization
        mapper.registerModule(new EntitySerializationModule());

        return mapper;
    }
}