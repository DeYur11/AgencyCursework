package org.example.advertisingagency;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@Component
public class StartupCheck {

    @PersistenceContext
    private EntityManager entityManager;

    @EventListener(ApplicationReadyEvent.class)
    public void printCurrentDatabaseName() {
        String dbName = (String) entityManager
                .createNativeQuery("SELECT DB_NAME()")
                .getSingleResult();

        System.out.println(">>> Connected to database: " + dbName);
    }
}
