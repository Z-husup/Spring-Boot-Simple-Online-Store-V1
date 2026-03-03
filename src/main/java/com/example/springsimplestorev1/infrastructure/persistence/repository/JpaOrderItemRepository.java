package com.example.springsimplestorev1.infrastructure.persistence.repository;

import com.example.springsimplestorev1.domain.model.OrderItem;
import com.example.springsimplestorev1.domain.repository.OrderItemRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOrderItemRepository extends JpaRepository<OrderItem, Long>, OrderItemRepository {
}
