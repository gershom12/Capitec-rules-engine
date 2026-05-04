package com.fraud.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fraud.dto.TransactionRequest;
import com.fraud.service.FraudOrchestratorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class TransactionConsumerTest {

    @Mock
    private FraudOrchestratorService orchestrator;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private TransactionConsumer consumer;

    @Test
    void should_process_valid_message() throws Exception {
        String json = "{\"userId\":\"u1\",\"amount\":100,\"location\":\"ZA\"}";

        TransactionRequest req = new TransactionRequest("u1", 100, "ZA");

        when(mapper.readValue(eq(json), eq(TransactionRequest.class)))
                .thenReturn(req);

        consumer.consume(json);

        verify(orchestrator).process(req);
    }

    @Test
    void should_throw_exception_on_invalid_json() throws Exception {
        when(mapper.readValue(anyString(), eq(TransactionRequest.class)))
                .thenThrow(new RuntimeException("Invalid JSON"));

        assertThrows(RuntimeException.class,
                () -> consumer.consume("bad json"));
    }

    @Test
    void should_throw_when_orchestrator_fails() throws Exception {
        String json = "{\"userId\":\"u1\",\"amount\":100,\"location\":\"ZA\"}";
        TransactionRequest req = new TransactionRequest("u1", 100, "ZA");

        when(mapper.readValue(anyString(), eq(TransactionRequest.class)))
                .thenReturn(req);

        doThrow(new RuntimeException("Processing failed"))
                .when(orchestrator).process(req);

        assertThrows(RuntimeException.class,
                () -> consumer.consume(json));
    }
}