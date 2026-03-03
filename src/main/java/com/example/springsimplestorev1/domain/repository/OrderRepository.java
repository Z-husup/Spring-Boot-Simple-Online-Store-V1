package com.example.springsimplestorev1.domain.repository;

import com.example.springsimplestorev1.domain.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(Long id);

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
}
