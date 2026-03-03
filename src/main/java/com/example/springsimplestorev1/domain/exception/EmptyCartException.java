package com.example.springsimplestorev1.domain.exception;

public class EmptyCartException extends BusinessRuleException {
    public EmptyCartException(String message) {
        super(message);
    }
}
