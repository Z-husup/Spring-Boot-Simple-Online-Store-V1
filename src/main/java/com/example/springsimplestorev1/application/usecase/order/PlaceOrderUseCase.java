package com.example.springsimplestorev1.application.usecase.order;

import com.example.springsimplestorev1.domain.exception.EmptyCartException;
import com.example.springsimplestorev1.domain.exception.ResourceNotFoundException;
import com.example.springsimplestorev1.domain.model.Cart;
import com.example.springsimplestorev1.domain.model.CartItem;
import com.example.springsimplestorev1.domain.model.Order;
import com.example.springsimplestorev1.domain.model.OrderItem;
import com.example.springsimplestorev1.domain.model.Product;
import com.example.springsimplestorev1.domain.model.User;
import com.example.springsimplestorev1.domain.repository.CartRepository;
import com.example.springsimplestorev1.domain.repository.OrderRepository;
import com.example.springsimplestorev1.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlaceOrderUseCase {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public PlaceOrderUseCase(OrderRepository orderRepository, CartRepository cartRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Order execute(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException("Cannot place order with an empty cart");
        }

        Order order = new Order(user);
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            product.decreaseStock(cartItem.getQuantity());
            order.addItem(new OrderItem(product, cartItem.getQuantity()));
        }

        Order savedOrder = orderRepository.save(order);
        cart.clear();
        return savedOrder;
    }
}