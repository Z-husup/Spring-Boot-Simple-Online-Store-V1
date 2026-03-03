package com.example.springsimplestorev1.infrastructure.persistence.repository;

import com.example.springsimplestorev1.domain.model.Order;
import com.example.springsimplestorev1.domain.repository.OrderRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaOrderRepository extends JpaRepository<Order, Long>, OrderRepository {

    @Override
    @Query("select o from Order o where o.user.id = :userId order by o.createdAt desc")
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
}
