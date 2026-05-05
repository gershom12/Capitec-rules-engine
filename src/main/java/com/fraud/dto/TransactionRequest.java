package com.fraud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest {

    @NotBlank(message = "userId is required")
    private String userId;

    @Positive(message = "amount must be greater than 0")
    private double amount;

    @NotBlank(message = "location is required")
    private String location;
}