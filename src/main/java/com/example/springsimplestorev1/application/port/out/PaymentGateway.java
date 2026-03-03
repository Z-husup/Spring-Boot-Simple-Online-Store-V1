package com.example.springsimplestorev1.application.port.out;

import java.util.List;

public interface PaymentGateway {

    CheckoutSession createCheckoutSession(CreateCheckoutSessionCommand command);

    PaymentSession getSession(String sessionId);

    record CreateCheckoutSessionCommand(
            String currency,
            String successUrl,
            String cancelUrl,
            List<LineItem> lineItems,
            String clientReferenceId
    ) {
    }

    record LineItem(
            String name,
            String imageUrl,
            long unitAmountCents,
            long quantity
    ) {
    }

    record CheckoutSession(
            String sessionId,
            String checkoutUrl
    ) {
    }

    record PaymentSession(
            String sessionId,
            String paymentStatus,
            String clientReferenceId
    ) {
    }
}
