package org.example.advertisingagency;

import org.example.advertisingagency.repository.WorkerRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class AdvertisingAgencyApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdvertisingAgencyApplication.class, args);
    }

}

