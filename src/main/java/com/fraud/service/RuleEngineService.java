package com.fraud.service;

import com.fraud.dto.RuleResult;
import com.fraud.entity.Transaction;
import com.fraud.rules.FraudRule;
import com.fraud.rules.RuleRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class RuleEngineService {

    private final RuleRegistry registry;

    public List<RuleResult> evaluate(Transaction transaction) {

        log.info("event=rule_engine status=START transactionId={}", transaction.getId());

        List<RuleResult> results = registry.getAllRules()
                .parallelStream()
                .map(rule -> executeRule(rule, transaction))
                .toList();

        log.info("event=rule_engine status=END transactionId={}", transaction.getId());

        return results;
    }

    private RuleResult executeRule(FraudRule rule, Transaction transaction) {
        try {
            RuleResult result = rule.evaluate(transaction);

            log.info("event=rule_executed rule={} triggered={}",
                    rule.getName(), result.isTriggered());

            return result;

        } catch (Exception e) {
            log.error("event=rule_execution_failed rule={} error={}",
                    rule.getName(), e.getMessage(), e);

            return new RuleResult(rule.getName(), false, "ERROR");
        }
    }
}