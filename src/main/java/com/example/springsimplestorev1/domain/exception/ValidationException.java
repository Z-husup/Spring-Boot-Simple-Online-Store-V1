package com.example.springsimplestorev1.domain.exception;

public class ValidationException extends DomainException {
    public ValidationException(String message) {
        super(message);
    }
}
