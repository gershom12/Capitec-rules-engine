package com.fraud.rules.impl;

import com.fraud.config.RuleConfig;
import com.fraud.dto.RuleResult;
import com.fraud.entity.Transaction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocationMismatchRuleTest {

    @Test
    void should_trigger_for_invalid_location() {
        RuleConfig config = new RuleConfig();
        RuleConfig.Location loc = new RuleConfig.Location();
        loc.setAllowedCountry("ZA");
        config.setLocation(loc);

        LocationMismatchRule rule = new LocationMismatchRule(config);

        Transaction tx = Transaction.builder().location("US").build();

        RuleResult result = rule.evaluate(tx);

        assertTrue(result.isTriggered());
    }

    @Test
    void should_not_trigger_for_valid_location() {
        RuleConfig config = new RuleConfig();
        RuleConfig.Location loc = new RuleConfig.Location();
        loc.setAllowedCountry("ZA");
        config.setLocation(loc);

        LocationMismatchRule rule = new LocationMismatchRule(config);

        Transaction tx = Transaction.builder().location("ZA").build();

        RuleResult result = rule.evaluate(tx);

        assertFalse(result.isTriggered());
    }
}