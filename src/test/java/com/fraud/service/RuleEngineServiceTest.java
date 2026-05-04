package com.fraud.service;

import com.fraud.dto.RuleResult;
import com.fraud.entity.Transaction;
import com.fraud.rules.FraudRule;
import com.fraud.rules.RuleRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class RuleEngineServiceTest {

    @Mock
    private RuleRegistry registry;

    @InjectMocks
    private RuleEngineService engine;

    @Test
    void should_execute_all_rules() {
        Transaction tx = Transaction.builder().id(1L).build();

        FraudRule rule1 = mock(FraudRule.class);
        FraudRule rule2 = mock(FraudRule.class);

        when(rule1.getName()).thenReturn("R1");
        when(rule2.getName()).thenReturn("R2");

        when(rule1.evaluate(tx)).thenReturn(new RuleResult("R1", false, "OK"));
        when(rule2.evaluate(tx)).thenReturn(new RuleResult("R2", true, "Fraud"));

        when(registry.getAllRules()).thenReturn(List.of(rule1, rule2));

        List<RuleResult> results = engine.evaluate(tx);

        assertEquals(2, results.size());
    }

    @Test
    void should_handle_rule_failure() {
        Transaction tx = Transaction.builder().id(1L).build();

        FraudRule brokenRule = mock(FraudRule.class);

        when(brokenRule.getName()).thenReturn("BROKEN");
        when(brokenRule.evaluate(tx)).thenThrow(new RuntimeException());

        when(registry.getAllRules()).thenReturn(List.of(brokenRule));

        List<RuleResult> results = engine.evaluate(tx);

        assertFalse(results.get(0).isTriggered());
        assertEquals("ERROR", results.get(0).getMessage());
    }

    @Test
    void should_handle_no_rules_registered() {
        Transaction tx = Transaction.builder().id(1L).build();

        when(registry.getAllRules()).thenReturn(List.of());

        List<RuleResult> results = engine.evaluate(tx);

        assertTrue(results.isEmpty());
    }
}