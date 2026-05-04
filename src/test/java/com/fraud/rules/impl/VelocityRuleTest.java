package com.fraud.rules.impl;

import com.fraud.config.RuleConfig;
import com.fraud.dto.RuleResult;
import com.fraud.entity.Transaction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VelocityRuleTest {

    private VelocityRule buildRule(int max, int window) {
        RuleConfig config = new RuleConfig();
        RuleConfig.Velocity v = new RuleConfig.Velocity();
        v.setMaxTransactions(max);
        v.setWindowSeconds(window);
        config.setVelocity(v);
        return new VelocityRule(config);
    }

    @Test
    void should_trigger_when_limit_exceeded() {
        VelocityRule rule = buildRule(2, 60);

        Transaction tx = Transaction.builder().userId("user1").build();

        rule.evaluate(tx);
        rule.evaluate(tx);
        RuleResult result = rule.evaluate(tx);

        assertTrue(result.isTriggered());
    }

    @Test
    void should_not_trigger_within_limit() {
        VelocityRule rule = buildRule(3, 60);

        Transaction tx = Transaction.builder().userId("user1").build();

        rule.evaluate(tx);
        RuleResult result = rule.evaluate(tx);

        assertFalse(result.isTriggered());
    }

    @Test
    void should_track_users_independently() {
        VelocityRule rule = buildRule(1, 60);

        Transaction u1 = Transaction.builder().userId("u1").build();
        Transaction u2 = Transaction.builder().userId("u2").build();

        rule.evaluate(u1);
        RuleResult result = rule.evaluate(u2);

        assertFalse(result.isTriggered());
    }
}