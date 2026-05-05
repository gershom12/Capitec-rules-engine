package com.fraud.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fraud.dto.TransactionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    @Value("${topics.transaction}")
    private String topic;

    public void send(Long transactionId, TransactionRequest request) {

        try {
            String message = mapper.writeValueAsString(
                    Map.of("transactionId", transactionId, "payload", request)
            );

            kafkaTemplate.send(topic, transactionId.toString(), message);

            log.info("event=kafka_produce status=SUCCESS transactionId={} topic={}",
                    transactionId, topic);

        } catch (Exception e) {
            log.error("event=kafka_produce status=FAILED transactionId={} error={}",
                    transactionId, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}