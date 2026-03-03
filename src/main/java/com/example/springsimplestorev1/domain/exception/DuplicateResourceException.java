package com.example.springsimplestorev1.domain.exception;

public class DuplicateResourceException extends BusinessRuleException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
