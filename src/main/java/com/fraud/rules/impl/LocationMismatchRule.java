package com.fraud.rules.impl;

import com.fraud.config.RuleConfig;
import com.fraud.dto.RuleResult;
import com.fraud.entity.Transaction;
import com.fraud.rules.FraudRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocationMismatchRule implements FraudRule {

    private final RuleConfig config;

    @Override
    public String getName() {
        return "LOCATION_MISMATCH";
    }

    @Override
    public RuleResult evaluate(Transaction transaction) {

        try {
            if (transaction == null) {
                log.error("event=rule_location status=FAILED reason=null_transaction");
                return error("Transaction is null");
            }

            if (transaction.getLocation() == null) {
                log.warn("event=rule_location status=SKIPPED reason=null_location");
                return error("Location missing");
            }

            String allowed = config.getLocation().getAllowedCountry();

            boolean triggered = !allowed.equalsIgnoreCase(transaction.getLocation());

            log.info("event=rule_location allowed={} actual={} triggered={}",
                    allowed, transaction.getLocation(), triggered);

            return RuleResult.builder()
                    .ruleName(getName())
                    .triggered(triggered)
                    .message(triggered
                            ? "Unexpected location: " + transaction.getLocation()
                            : "Location valid")
                    .build();

        } catch (Exception e) {
            log.error("event=rule_location status=ERROR error={}", e.getMessage(), e);
            return error("Error evaluating location rule");
        }
    }

    private RuleResult error(String msg) {
        return RuleResult.builder()
                .ruleName(getName())
                .triggered(false)
                .message(msg)
                .build();
    }
}