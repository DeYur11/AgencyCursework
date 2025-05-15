package org.example.advertisingagency.publisher;

import lombok.extern.slf4j.Slf4j;
import org.example.advertisingagency.model.log.TransactionLog;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Predicate;

/**
 * Publisher for real-time transaction events.
 * This replaces the AuditLogPublisher with enhanced functionality.
 */
@Slf4j
@Component
public class TransactionPublisher {
    private final Sinks.Many<TransactionLog> sink;

    public TransactionPublisher() {
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
    }

    /**
     * Publish a transaction log to subscribers.
     *
     * @param transactionLog The transaction log to publish
     */
    public void publish(TransactionLog transactionLog) {
        log.info("Publishing transaction: {} - {}", transactionLog.getAction(), transactionLog.getEntityType());
        Sinks.EmitResult result = sink.tryEmitNext(transactionLog);

        if (result.isFailure()) {
            log.error("Failed to publish transaction: {}", result);
        }
    }

    /**
     * Get a filtered stream of transaction logs.
     *
     * @param filter The predicate to filter the logs
     * @return A flux of filtered transaction logs
     */
    public Flux<TransactionLog> getFilteredStream(Predicate<TransactionLog> filter) {
        return sink.asFlux().filter(filter);
    }
}