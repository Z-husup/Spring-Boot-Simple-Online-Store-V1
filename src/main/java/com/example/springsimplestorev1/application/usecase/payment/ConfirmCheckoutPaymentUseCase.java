package com.example.springsimplestorev1.application.usecase.payment;

import com.example.springsimplestorev1.application.port.out.PaymentGateway;
import com.example.springsimplestorev1.application.usecase.order.PlaceOrderUseCase;
import com.example.springsimplestorev1.domain.exception.BusinessRuleException;
import com.example.springsimplestorev1.domain.model.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfirmCheckoutPaymentUseCase {

    private final PaymentGateway paymentGateway;
    private final PlaceOrderUseCase placeOrderUseCase;

    public ConfirmCheckoutPaymentUseCase(PaymentGateway paymentGateway, PlaceOrderUseCase placeOrderUseCase) {
        this.paymentGateway = paymentGateway;
        this.placeOrderUseCase = placeOrderUseCase;
    }

    @Transactional
    public Order execute(Long userId, String sessionId) {
        PaymentGateway.PaymentSession session = paymentGateway.getSession(sessionId);
        if (!String.valueOf(userId).equals(session.clientReferenceId())) {
            throw new BusinessRuleException("Payment session does not belong to the current user");
        }
        if (!"paid".equalsIgnoreCase(session.paymentStatus())) {
            throw new BusinessRuleException("Payment is not completed");
        }

        return placeOrderUseCase.execute(userId);
    }
}
