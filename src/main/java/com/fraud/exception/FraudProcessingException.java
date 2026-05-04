package com.fraud.exception;

public class FraudProcessingException extends RuntimeException {
    public FraudProcessingException(String msg, Throwable cause) {
        super(msg, cause);
    }
}