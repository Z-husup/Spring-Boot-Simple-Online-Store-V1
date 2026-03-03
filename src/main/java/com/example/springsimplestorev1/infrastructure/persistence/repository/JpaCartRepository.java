package com.example.springsimplestorev1.infrastructure.persistence.repository;

import com.example.springsimplestorev1.domain.model.Cart;
import com.example.springsimplestorev1.domain.repository.CartRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCartRepository extends JpaRepository<Cart, Long>, CartRepository {
}
