package com.fraud.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeadLetterConsumer {

    @KafkaListener(topics = "${topics.dlq}")
    public void listen(String message) {
        log.error("event=dlq_message_received payload={}", message);

    }
}