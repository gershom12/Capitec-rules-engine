package com.fraud.service;

import com.fraud.dto.FraudResponse;
import com.fraud.dto.RuleResult;
import com.fraud.dto.TransactionRequest;
import com.fraud.entity.Transaction;
import com.fraud.exception.FraudProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudOrchestratorService {

    private final RuleEngineService ruleEngine;
    private final FraudService fraudService;

    public void process(Long transactionId, TransactionRequest request) {

        try {
            log.info("event=process_transaction status=START transactionId={}", transactionId);

            Transaction tx = fraudService.getById(transactionId);

            List<RuleResult> results = ruleEngine.evaluate(tx);

            boolean fraudDetected =
                    results.stream().anyMatch(RuleResult::isTriggered);

            log.info("event=fraud_evaluation transactionId={} fraudDetected={}",
                    transactionId, fraudDetected);

            if (fraudDetected) {
                fraudService.saveAlert(tx, results);
                log.warn("event=fraud_detected transactionId={}", transactionId);
            }

            fraudService.markCompleted(tx);

            log.info("event=process_transaction status=COMPLETED transactionId={}", transactionId);

        } catch (Exception e) {
            log.error("event=process_transaction status=FAILED transactionId={} error={}",
                    transactionId, e.getMessage(), e);

            fraudService.markFailed(transactionId);
            throw new RuntimeException(e);
        }
    }
}