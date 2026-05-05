package com.fraud.controller;

import com.fraud.dto.TransactionRequest;
import com.fraud.entity.Transaction;
import com.fraud.kafka.TransactionProducer;
import com.fraud.service.FraudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/fraud")
@RequiredArgsConstructor
@Slf4j
public class FraudController {

    private final FraudService fraudService;
    private final TransactionProducer producer;

    @PostMapping("/check")
    public ResponseEntity<?> check(@Valid @RequestBody TransactionRequest request) {

        log.info("event=api_request status=START userId={} amount={} location={}",
                request.getUserId(), request.getAmount(), request.getLocation());

        Transaction tx = fraudService.createTransaction(request);

        producer.send(tx.getId(), request);

        log.info("event=api_request status=QUEUED transactionId={}", tx.getId());

        return ResponseEntity.accepted().body(
                Map.of(
                        "transactionId", tx.getId(),
                        "status", tx.getStatus(),
                        "message", "Transaction queued for processing"
                )
        );
    }
}