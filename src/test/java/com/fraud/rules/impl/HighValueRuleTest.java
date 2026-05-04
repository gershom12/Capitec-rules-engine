package com.fraud.rules.impl;

import com.fraud.config.RuleConfig;
import com.fraud.dto.RuleResult;
import com.fraud.entity.Transaction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HighValueRuleTest {

    @Test
    void should_trigger_when_above_threshold() {
        RuleConfig config = new RuleConfig();
        RuleConfig.HighValue hv = new RuleConfig.HighValue();
        hv.setThreshold(1000);
        config.setHighValue(hv);

        HighValueRule rule = new HighValueRule(config);

        Transaction tx = Transaction.builder().amount(2000).build();

        RuleResult result = rule.evaluate(tx);

        assertTrue(result.isTriggered());
    }

    @Test
    void should_not_trigger_when_below_threshold() {
        RuleConfig config = new RuleConfig();
        RuleConfig.HighValue hv = new RuleConfig.HighValue();
        hv.setThreshold(1000);
        config.setHighValue(hv);

        HighValueRule rule = new HighValueRule(config);

        Transaction tx = Transaction.builder().amount(500).build();

        RuleResult result = rule.evaluate(tx);

        assertFalse(result.isTriggered());
    }
}