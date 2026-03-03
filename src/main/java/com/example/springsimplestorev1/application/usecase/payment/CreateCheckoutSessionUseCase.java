package com.example.springsimplestorev1.application.usecase.payment;

import com.example.springsimplestorev1.application.port.out.PaymentGateway;
import com.example.springsimplestorev1.domain.exception.EmptyCartException;
import com.example.springsimplestorev1.domain.exception.ResourceNotFoundException;
import com.example.springsimplestorev1.domain.model.Cart;
import com.example.springsimplestorev1.domain.repository.CartItemRepository;
import com.example.springsimplestorev1.domain.repository.CartRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreateCheckoutSessionUseCase {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final PaymentGateway paymentGateway;
    private final String currency;

    public CreateCheckoutSessionUseCase(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            PaymentGateway paymentGateway,
            @Value("${payment.stripe.currency:usd}") String currency
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.paymentGateway = paymentGateway;
        this.currency = currency;
    }

    public CheckoutSessionResult execute(Long userId, String successUrl, String cancelUrl) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        var cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new EmptyCartException("Cannot checkout with an empty cart");
        }

        List<PaymentGateway.LineItem> lineItems = cartItems.stream()
                .map(item -> new PaymentGateway.LineItem(
                        item.getProduct().getName(),
                        item.getProduct().getImageUrl(),
                        Math.round(item.getProduct().getPrice() * 100),
                        item.getQuantity()
                ))
                .toList();

        PaymentGateway.CheckoutSession session = paymentGateway.createCheckoutSession(
                new PaymentGateway.CreateCheckoutSessionCommand(
                        currency,
                        successUrl,
                        cancelUrl,
                        lineItems,
                        String.valueOf(userId)
                )
        );

        return new CheckoutSessionResult(session.sessionId(), session.checkoutUrl());
    }

    public record CheckoutSessionResult(String sessionId, String checkoutUrl) {
    }
}
