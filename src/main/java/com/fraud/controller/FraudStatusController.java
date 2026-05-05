package com.fraud.controller;

import com.fraud.entity.FraudAlert;
import com.fraud.repository.FraudAlertRepository;
import com.fraud.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/status")
@RequiredArgsConstructor
@Slf4j
public class FraudStatusController {

    private final TransactionRepository txRepo;
    private final FraudAlertRepository alertRepo;

    // ----------------------------------------
    // 1. BASIC STATUS
    // ----------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getStatus(@PathVariable Long id) {

        log.info("event=status_check transactionId={}", id);

        return txRepo.findById(id)
                .map(tx -> {

                    List<FraudAlert> alerts = alertRepo.findAll()
                            .stream()
                            .filter(a -> id.equals(a.getTransactionId()))
                            .toList();

                    boolean fraudDetected = !alerts.isEmpty();

                    log.info("event=status_result transactionId={} status={} fraudDetected={}",
                            id, tx.getStatus(), fraudDetected);

                    return ResponseEntity.ok(
                            Map.of(
                                    "transactionId", id,
                                    "status", tx.getStatus(),
                                    "fraudDetected", fraudDetected,
                                    "alertCount", alerts.size()
                            )
                    );
                })
                .orElseGet(() -> {
                    log.warn("event=status_not_found transactionId={}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    // ----------------------------------------
    // 2. FRAUD DETAILS (ACTUAL FRAUD COMMITTED)
    // ----------------------------------------
    @GetMapping("/{id}/details")
    public ResponseEntity<?> getFraudDetails(@PathVariable Long id) {

        log.info("event=fraud_details_check transactionId={}", id);

        List<FraudAlert> alerts = alertRepo.findAll()
                .stream()
                .filter(a -> id.equals(a.getTransactionId()))
                .toList();

        if (alerts.isEmpty()) {
            log.info("event=no_fraud_found transactionId={}", id);

            return ResponseEntity.ok(
                    Map.of(
                            "transactionId", id,
                            "fraudDetected", false,
                            "message", "No fraud detected",
                            "rulesTriggered", List.of()
                    )
            );
        }

        List<String> rulesTriggered = alerts.stream()
                .map(FraudAlert::getRulesTriggered)
                .collect(Collectors.toList());

        log.warn("event=fraud_details_found transactionId={} rules={}",
                id, rulesTriggered);

        return ResponseEntity.ok(
                Map.of(
                        "transactionId", id,
                        "fraudDetected", true,
                        "rulesTriggered", rulesTriggered,
                        "alerts", alerts
                )
        );
    }
}