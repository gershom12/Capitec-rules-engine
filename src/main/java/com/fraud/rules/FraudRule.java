package com.fraud.rules;

import com.fraud.dto.RuleResult;
import com.fraud.entity.Transaction;

public interface FraudRule {

    String getName();

    RuleResult evaluate(Transaction transaction);
}