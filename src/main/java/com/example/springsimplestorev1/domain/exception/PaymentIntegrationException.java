package com.example.springsimplestorev1.domain.exception;

public class PaymentIntegrationException extends DomainException {
    public PaymentIntegrationException(String message) {
        super(message);
    }

    public PaymentIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
