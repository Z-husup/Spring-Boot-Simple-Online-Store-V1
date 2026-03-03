package com.example.springsimplestorev1.application.usecase.cart;

import com.example.springsimplestorev1.domain.exception.ResourceNotFoundException;
import com.example.springsimplestorev1.domain.model.Cart;
import com.example.springsimplestorev1.domain.model.User;
import com.example.springsimplestorev1.domain.repository.CartRepository;
import com.example.springsimplestorev1.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetOrCreateCartUseCase {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public GetOrCreateCartUseCase(CartRepository cartRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Cart execute(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
                    return cartRepository.save(new Cart(user));
                });
    }
}