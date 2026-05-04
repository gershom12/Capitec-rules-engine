package com.fraud.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fraud.dto.TransactionRequest;
import com.fraud.exception.FraudProcessingException;
import com.fraud.service.FraudOrchestratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionConsumer {

    private final FraudOrchestratorService orchestrator;
    private final ObjectMapper mapper;

    @KafkaListener(topics = "${topics.transaction}", groupId = "fraud-group")
    public void consume(String message) {

        log.info("event=kafka_consume status=RECEIVED payload={}", message);

        try {
            TransactionRequest request =
                    mapper.readValue(message, TransactionRequest.class);

            orchestrator.process(request);

            log.info("event=kafka_consume status=SUCCESS");

        } catch (Exception e) {
            log.error("event=kafka_consume status=FAILED error={}", e.getMessage(), e);

            // IMPORTANT: rethrow → triggers retry + DLQ
            throw new FraudProcessingException("Kafka processing failed", e);
        }
    }
}