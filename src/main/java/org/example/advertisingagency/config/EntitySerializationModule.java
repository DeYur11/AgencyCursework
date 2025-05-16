package org.example.advertisingagency.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

import java.io.IOException;
import java.util.List;

/**
 * Custom Jackson module for properly serializing entity relationships
 * to handle transaction logging and rollbacks.
 */
public class EntitySerializationModule extends SimpleModule {

    public EntitySerializationModule() {
        super("EntitySerializationModule");

        // Register serializer for HibernateProxy objects
        addSerializer(HibernateProxy.class, new HibernateProxySerializer());

        // Modify bean serializer to handle lazy loading
        setSerializerModifier(new EntityBeanSerializerModifier());
    }

    /**
     * Custom serializer for Hibernate proxies that extracts the entity ID
     * rather than trying to serialize the entire proxy.
     */
    static class HibernateProxySerializer extends JsonSerializer<HibernateProxy> {
        @Override
        public void serialize(HibernateProxy value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value == null) {
                gen.writeNull();
                return;
            }

            LazyInitializer initializer = value.getHibernateLazyInitializer();
            Object id = initializer.getIdentifier();

            gen.writeStartObject();
            gen.writeObjectField("id", id);
            gen.writeStringField("_entityName", initializer.getEntityName());
            gen.writeEndObject();
        }
    }

    /**
     * Custom bean serializer modifier that handles lazy loading of entity properties
     * by only serializing the ID of related entities.
     */
    static class EntityBeanSerializerModifier extends BeanSerializerModifier {
        @Override
        public List<BeanPropertyWriter> changeProperties(
                SerializationConfig config,
                BeanDescription beanDesc,
                List<BeanPropertyWriter> beanProperties) {

            // Modify each property writer to handle lazy loading
            for (BeanPropertyWriter writer : beanProperties) {
                writer.assignNullSerializer(new EntityReferenceSerializer());
            }

            return beanProperties;
        }
    }

    /**
     * Custom serializer that handles null values and entity references.
     */
    static class EntityReferenceSerializer extends JsonSerializer<Object> {
        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            // Just write null for null values or uninitialized proxies
            gen.writeNull();
        }
    }
}