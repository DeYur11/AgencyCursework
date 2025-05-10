package org.example.advertisingagency.service.logs;

import lombok.extern.slf4j.Slf4j;
import org.example.advertisingagency.model.log.AuditLog;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Predicate;

@Slf4j
@Component
public class AuditLogPublisher {
    private final Sinks.Many<AuditLog> sink;

    public AuditLogPublisher() {
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
    }

    public void publish(AuditLog log) {
        AuditLogPublisher.log.info(log.toString());
        sink.tryEmitNext(log);
    }

    public Flux<AuditLog> getFilteredStream(Predicate<AuditLog> filter) {
        return sink.asFlux()
                .filter(filter);
    }
}
