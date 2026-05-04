package com.fraud.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@Slf4j
public class KafkaErrorHandler {

    @Value("${topics.dlq}")
    private String dlqTopic;

    @Value("${fraud.retry.attempts}")
    private int retryAttempts;

    @Value("${fraud.retry.backoff-ms}")
    private long backoffMs;

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, String> kafkaTemplate) {

        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        kafkaTemplate,
                        (record, ex) -> {

                            log.error(
                                    "event=dlq_publish originalTopic={} partition={} offset={} error={}",
                                    record.topic(),
                                    record.partition(),
                                    record.offset(),
                                    ex.getMessage(),
                                    ex
                            );

                            return new TopicPartition(dlqTopic, record.partition());
                        });

        FixedBackOff backOff = new FixedBackOff(backoffMs, retryAttempts);

        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backOff);

        // Retry only for recoverable exceptions
        handler.addRetryableExceptions(RuntimeException.class);

        // Do NOT retry fatal ones
        handler.addNotRetryableExceptions(IllegalArgumentException.class);

        log.info("event=kafka_error_handler_initialized retries={} backoffMs={} dlq={}",
                retryAttempts, backoffMs, dlqTopic);

        return handler;
    }
}