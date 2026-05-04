package com.fraud.service;

import com.fraud.dto.RuleResult;
import com.fraud.dto.TransactionRequest;
import com.fraud.entity.*;
import com.fraud.exception.FraudProcessingException;
import com.fraud.repository.FraudAlertRepository;
import com.fraud.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudService {

    private final TransactionRepository transactionRepo;
    private final FraudAlertRepository alertRepo;

    public Transaction saveTransaction(TransactionRequest req) {

        try {
            Transaction tx = Transaction.builder()
                    .userId(req.getUserId())
                    .amount(req.getAmount())
                    .location(req.getLocation())
                    .timestamp(LocalDateTime.now())
                    .build();

            Transaction saved = transactionRepo.save(tx);

            log.info("event=transaction_saved id={}", saved.getId());

            return saved;

        } catch (Exception e) {
            log.error("event=transaction_save_failed error={}", e.getMessage(), e);
            throw new FraudProcessingException("DB error saving transaction", e);
        }
    }

    public void saveAlert(Transaction tx, List<RuleResult> results) {

        try {
            FraudAlert alert = FraudAlert.builder()
                    .transactionId(tx.getId())
                    .rulesTriggered(results.toString())
                    .createdAt(LocalDateTime.now())
                    .build();

            alertRepo.save(alert);

            log.warn("event=fraud_alert_saved transactionId={}", tx.getId());

        } catch (Exception e) {
            log.error("event=alert_save_failed error={}", e.getMessage(), e);
            throw new FraudProcessingException("DB error saving alert", e);
        }
    }
}