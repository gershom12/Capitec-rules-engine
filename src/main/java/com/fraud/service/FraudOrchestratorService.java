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

    public FraudResponse process(TransactionRequest request) {

        try {
            log.info("event=process_transaction status=START userId={}", request.getUserId());

            Transaction transaction = fraudService.saveTransaction(request);

            List<RuleResult> results = ruleEngine.evaluate(transaction);

            boolean fraudDetected = results.stream().anyMatch(RuleResult::isTriggered);

            if (fraudDetected) {
                fraudService.saveAlert(transaction, results);
                log.warn("event=fraud_detected transactionId={}", transaction.getId());
            }

            log.info("event=process_transaction status=SUCCESS transactionId={}", transaction.getId());

            return new FraudResponse(transaction.getId(), fraudDetected, results);

        } catch (Exception exception) {
            log.error("event=process_transaction status=FAILED error={}", exception.getMessage(), exception);
            throw new FraudProcessingException("Failed to process transaction", exception);
        }
    }
}