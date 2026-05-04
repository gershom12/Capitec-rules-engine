package com.fraud.service;

import com.fraud.dto.*;
import com.fraud.entity.Transaction;
import com.fraud.exception.FraudProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class FraudOrchestratorServiceTest {

    @Mock
    private RuleEngineService ruleEngine;

    @Mock
    private FraudService fraudService;

    @InjectMocks
    private FraudOrchestratorService orchestrator;

    @Test
    void should_detect_fraud_and_save_alert() {
        TransactionRequest request = new TransactionRequest("user1", 15000, "ZA");

        Transaction tx = Transaction.builder().id(1L).build();

        List<RuleResult> results = List.of(
                new RuleResult("HIGH_VALUE", true, "Triggered")
        );

        when(fraudService.saveTransaction(request)).thenReturn(tx);
        when(ruleEngine.evaluate(tx)).thenReturn(results);

        FraudResponse response = orchestrator.process(request);

        assertTrue(response.isFraudDetected());
        verify(fraudService).saveAlert(tx, results);
    }

    @Test
    void should_not_save_alert_when_no_fraud() {
        TransactionRequest request = new TransactionRequest("user1", 100, "ZA");

        Transaction tx = Transaction.builder().id(1L).build();

        when(fraudService.saveTransaction(request)).thenReturn(tx);
        when(ruleEngine.evaluate(tx)).thenReturn(List.of(
                new RuleResult("HIGH_VALUE", false, "OK")
        ));

        FraudResponse response = orchestrator.process(request);

        assertFalse(response.isFraudDetected());
        verify(fraudService, never()).saveAlert(any(), any());
    }

    @Test
    void should_throw_exception_when_transaction_save_fails() {
        TransactionRequest request = new TransactionRequest("user1", 100, "ZA");

        when(fraudService.saveTransaction(request))
                .thenThrow(new RuntimeException("DB down"));

        assertThrows(FraudProcessingException.class,
                () -> orchestrator.process(request));
    }

    @Test
    void should_handle_empty_rule_results() {
        TransactionRequest request = new TransactionRequest("user1", 100, "ZA");

        Transaction tx = Transaction.builder().id(1L).build();

        when(fraudService.saveTransaction(request)).thenReturn(tx);
        when(ruleEngine.evaluate(tx)).thenReturn(List.of());

        FraudResponse response = orchestrator.process(request);

        assertFalse(response.isFraudDetected());
    }
}