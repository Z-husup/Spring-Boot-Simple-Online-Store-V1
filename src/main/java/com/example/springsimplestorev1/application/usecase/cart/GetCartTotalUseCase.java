package com.example.springsimplestorev1.application.usecase.cart;

import com.example.springsimplestorev1.domain.exception.ResourceNotFoundException;
import com.example.springsimplestorev1.domain.model.Cart;
import com.example.springsimplestorev1.domain.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetCartTotalUseCase {

    private final CartRepository cartRepository;

    public GetCartTotalUseCase(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Transactional(readOnly = true)
    public double execute(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));
        return cart.getTotal();
    }
}
