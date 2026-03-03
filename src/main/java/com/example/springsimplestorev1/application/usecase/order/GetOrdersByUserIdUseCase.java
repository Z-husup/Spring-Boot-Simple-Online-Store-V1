package com.example.springsimplestorev1.application.usecase.order;

import com.example.springsimplestorev1.domain.exception.ResourceNotFoundException;
import com.example.springsimplestorev1.domain.model.Order;
import com.example.springsimplestorev1.domain.repository.OrderRepository;
import com.example.springsimplestorev1.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetOrdersByUserIdUseCase {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public GetOrdersByUserIdUseCase(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    public List<Order> execute(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}