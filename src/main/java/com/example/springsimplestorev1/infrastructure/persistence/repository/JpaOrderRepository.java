package com.example.springsimplestorev1.infrastructure.persistence.repository;

import com.example.springsimplestorev1.domain.model.Order;
import com.example.springsimplestorev1.domain.repository.OrderRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOrderRepository extends JpaRepository<Order, Long>, OrderRepository {
}
