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
public class HighValueRule implements FraudRule {

    private final RuleConfig config;

    @Override
    public String getName() {
        return "HIGH_VALUE";
    }

    @Override
    public RuleResult evaluate(Transaction transaction) {

        try {
            if (transaction == null) {
                log.error("event=rule_high_value status=FAILED reason=null_transaction");
                return error("Transaction is null");
            }

            double threshold = config.getHighValue().getThreshold();

            boolean triggered = transaction.getAmount() > threshold;

            log.info("event=rule_high_value amount={} threshold={} triggered={}",
                    transaction.getAmount(), threshold, triggered);

            return RuleResult.builder()
                    .ruleName(getName())
                    .triggered(triggered)
                    .message(triggered
                            ? "Amount exceeds threshold: " + threshold
                            : "Amount within limit")
                    .build();

        } catch (Exception e) {
            log.error("event=rule_high_value status=ERROR error={}", e.getMessage(), e);
            return error("Error evaluating high value rule");
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