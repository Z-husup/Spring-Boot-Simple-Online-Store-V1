package com.example.springsimplestorev1.domain.repository;

import com.example.springsimplestorev1.domain.model.Cart;

import java.util.Optional;

public interface CartRepository {

    Cart save(Cart cart);

    Optional<Cart> findById(Long id);

    Optional<Cart> findByUserId(Long userId);
}
