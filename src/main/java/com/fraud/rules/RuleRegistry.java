package com.fraud.rules;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class RuleRegistry {

    private final List<FraudRule> rules;

    public RuleRegistry(List<FraudRule> rules) {
        this.rules = rules;

        log.info("event=rule_registry_initialized totalRules={}", rules.size());

        rules.forEach(rule ->
                log.info("event=rule_registered name={}", rule.getName()));
    }

    public List<FraudRule> getAllRules() {

        if (rules == null || rules.isEmpty()) {
            log.warn("event=no_rules_registered");
            return Collections.emptyList();
        }

        return rules;
    }
}