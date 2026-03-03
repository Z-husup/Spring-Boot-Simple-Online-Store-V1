package com.example.springsimplestorev1.infrastructure.persistence.repository;

import com.example.springsimplestorev1.domain.model.CartItem;
import com.example.springsimplestorev1.domain.repository.CartItemRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCartItemRepository extends JpaRepository<CartItem, Long>, CartItemRepository {
}
