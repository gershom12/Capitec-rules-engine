package com.fraud.service;

import com.fraud.dto.RuleResult;
import com.fraud.dto.TransactionRequest;
import com.fraud.entity.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FraudOrchestratorServiceTest {

    @Mock
    private RuleEngineService ruleEngine;

    @Mock
    private FraudService fraudService;

    @InjectMocks
    private FraudOrchestratorService orchestrator;

    // ----------------------------
    // 1. NO FRAUD SCENARIO
    // ----------------------------
    @Test
    void should_complete_transaction_when_no_fraud_detected() {

        Long txId = 1L;
        Transaction tx = Transaction.builder().id(txId).build();

        when(fraudService.getById(txId)).thenReturn(tx);

        when(ruleEngine.evaluate(tx)).thenReturn(
                List.of(new RuleResult("HIGH_VALUE", false, "OK"))
        );

        orchestrator.process(txId, new TransactionRequest());

        verify(fraudService).getById(txId);
        verify(ruleEngine).evaluate(tx);
        verify(fraudService).markCompleted(tx);
        verify(fraudService, never()).saveAlert(any(), any());
        verify(fraudService, never()).markFailed(anyLong());
    }

    // ----------------------------
    // 2. FRAUD DETECTED SCENARIO
    // ----------------------------
    @Test
    void should_save_alert_when_fraud_detected() {

        Long txId = 2L;
        Transaction tx = Transaction.builder().id(txId).build();

        when(fraudService.getById(txId)).thenReturn(tx);

        when(ruleEngine.evaluate(tx)).thenReturn(
                List.of(new RuleResult("HIGH_VALUE", true, "Triggered"))
        );

        orchestrator.process(txId, new TransactionRequest());

        verify(fraudService).saveAlert(eq(tx), any());
        verify(fraudService).markCompleted(tx);
    }

    // ----------------------------
    // 3. FAILURE SCENARIO
    // ----------------------------
    @Test
    void should_mark_failed_when_exception_occurs() {

        Long txId = 3L;

        when(fraudService.getById(txId))
                .thenThrow(new RuntimeException("DB failure"));

        assertThrows(RuntimeException.class,
                () -> orchestrator.process(txId, new TransactionRequest()));

        verify(fraudService).markFailed(txId);
    }

    // ----------------------------
    // 4. MULTIPLE RULES SCENARIO
    // ----------------------------
    @Test
    void should_handle_multiple_rules_correctly() {

        Long txId = 4L;
        Transaction tx = Transaction.builder().id(txId).build();

        when(fraudService.getById(txId)).thenReturn(tx);

        when(ruleEngine.evaluate(tx)).thenReturn(List.of(
                new RuleResult("HIGH_VALUE", true, "High value"),
                new RuleResult("LOCATION", false, "OK"),
                new RuleResult("VELOCITY", false, "OK")
        ));

        orchestrator.process(txId, new TransactionRequest());

        verify(fraudService).saveAlert(eq(tx), any());
        verify(fraudService).markCompleted(tx);
    }
}