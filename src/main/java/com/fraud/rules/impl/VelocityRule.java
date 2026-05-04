package com.fraud.rules.impl;

import com.fraud.config.RuleConfig;
import com.fraud.dto.RuleResult;
import com.fraud.entity.Transaction;
import com.fraud.rules.FraudRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class VelocityRule implements FraudRule {

    private final RuleConfig config;

    private final Map<String, List<LocalDateTime>> cache = new ConcurrentHashMap<>();

    @Override
    public String getName() {
        return "VELOCITY";
    }

    @Override
    public RuleResult evaluate(Transaction transaction) {

        try {
            if (transaction == null) {
                log.error("event=rule_velocity status=FAILED reason=null_transaction");
                return error("Transaction is null");
            }

            if (transaction.getUserId() == null) {
                log.warn("event=rule_velocity status=SKIPPED reason=null_user");
                return error("User ID missing");
            }

            int maxTx = config.getVelocity().getMaxTransactions();
            int window = config.getVelocity().getWindowSeconds();

            LocalDateTime now = LocalDateTime.now();

            cache.putIfAbsent(transaction.getUserId(), new ArrayList<>());
            List<LocalDateTime> timestamps = cache.get(transaction.getUserId());

            // cleanup old entries
            timestamps.removeIf(t -> t.isBefore(now.minusSeconds(window)));

            timestamps.add(now);

            boolean triggered = timestamps.size() > maxTx;

            log.info("event=rule_velocity userId={} count={} window={} triggered={}",
                    transaction.getUserId(), timestamps.size(), window, triggered);

            return RuleResult.builder()
                    .ruleName(getName())
                    .triggered(triggered)
                    .message(triggered
                            ? "Too many transactions in " + window + " seconds"
                            : "Velocity normal")
                    .build();

        } catch (Exception e) {
            log.error("event=rule_velocity status=ERROR error={}", e.getMessage(), e);
            return error("Error evaluating velocity rule");
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