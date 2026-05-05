package com.fraud.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fraud.dto.TransactionRequest;
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
            JsonNode node = mapper.readTree(message);

            Long transactionId = node.get("transactionId").asLong();

            TransactionRequest request =
                    mapper.treeToValue(node.get("payload"), TransactionRequest.class);

            log.info("event=kafka_consume status=PROCESSING transactionId={}", transactionId);

            orchestrator.process(transactionId, request);

            log.info("event=kafka_consume status=SUCCESS transactionId={}", transactionId);

        } catch (Exception e) {
            log.error("event=kafka_consume status=FAILED error={}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}