package com.fraud.dto;

import com.fraud.dto.RuleResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudResponse {

    private Long transactionId;
    private boolean fraudDetected;
    private List<RuleResult> rules;
}