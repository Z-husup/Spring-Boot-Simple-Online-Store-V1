package com.example.springsimplestorev1.domain.repository;

import com.example.springsimplestorev1.domain.model.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository {

    CartItem save(CartItem cartItem);

    Optional<CartItem> findById(Long id);

    List<CartItem> findByCartId(Long cartId);

    void deleteByCartIdAndProductId(Long cartId, Long productId);
}
