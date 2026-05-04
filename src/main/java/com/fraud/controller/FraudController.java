package com.fraud.controller;

import com.fraud.dto.FraudResponse;
import com.fraud.dto.TransactionRequest;
import com.fraud.service.FraudOrchestratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fraud")
@RequiredArgsConstructor
@Slf4j
public class FraudController {

    private final FraudOrchestratorService orchestrator;

    @PostMapping("/check")
    public ResponseEntity<FraudResponse> check(@RequestBody TransactionRequest request) {

        log.info("event=api_request endpoint=/check userId={}", request.getUserId());

        return ResponseEntity.ok(orchestrator.process(request));
    }
}