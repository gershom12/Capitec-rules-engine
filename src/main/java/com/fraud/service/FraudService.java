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

    private final TransactionRepository txRepo;
    private final FraudAlertRepository alertRepo;

    public Transaction createTransaction(TransactionRequest req) {

        Transaction tx = Transaction.builder()
                .userId(req.getUserId())
                .amount(req.getAmount())
                .location(req.getLocation())
                .timestamp(LocalDateTime.now())
                .status("PROCESSING")
                .build();

        Transaction saved = txRepo.save(tx);

        log.info("event=db_insert transactionId={} userId={}",
                saved.getId(), saved.getUserId());

        return saved;
    }

    public Transaction getById(Long id) {
        return txRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public void markCompleted(Transaction tx) {
        tx.setStatus("COMPLETED");
        txRepo.save(tx);

        log.info("event=transaction_completed transactionId={}", tx.getId());
    }

    public void markFailed(Long id) {
        txRepo.findById(id).ifPresent(tx -> {
            tx.setStatus("FAILED");
            txRepo.save(tx);

            log.error("event=transaction_failed transactionId={}", id);
        });
    }

    public void saveAlert(Transaction tx, List<RuleResult> results) {

        FraudAlert alert = FraudAlert.builder()
                .transactionId(tx.getId())
                .rulesTriggered(results.toString())
                .createdAt(LocalDateTime.now())
                .build();

        alertRepo.save(alert);

        log.warn("event=fraud_alert_saved transactionId={} rules={}",
                tx.getId(), results);
    }
}