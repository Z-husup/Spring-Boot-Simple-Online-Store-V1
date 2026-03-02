package com.example.springsimplestorev1.domain.repository;

import com.example.springsimplestorev1.domain.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCartId(Long cartId);

    void deleteByCartIdAndProductId(Long cartId, Long productId);
}
