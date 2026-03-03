package com.example.springsimplestorev1.domain.exception;

public class InsufficientStockException extends BusinessRuleException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
