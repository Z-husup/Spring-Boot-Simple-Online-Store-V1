package com.example.springsimplestorev1.infrastructure.web;

import com.example.springsimplestorev1.application.usecase.order.GetOrderByIdUseCase;
import com.example.springsimplestorev1.application.usecase.order.GetOrdersByUserIdUseCase;
import com.example.springsimplestorev1.application.usecase.order.PlaceOrderUseCase;
import com.example.springsimplestorev1.domain.model.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/client/orders")
@PreAuthorize("hasAnyRole('CLIENT','ADMIN')")
public class OrderController {

    private final PlaceOrderUseCase placeOrderUseCase;
    private final GetOrderByIdUseCase getOrderByIdUseCase;
    private final GetOrdersByUserIdUseCase getOrdersByUserIdUseCase;

    public OrderController(
            PlaceOrderUseCase placeOrderUseCase,
            GetOrderByIdUseCase getOrderByIdUseCase,
            GetOrdersByUserIdUseCase getOrdersByUserIdUseCase
    ) {
        this.placeOrderUseCase = placeOrderUseCase;
        this.getOrderByIdUseCase = getOrderByIdUseCase;
        this.getOrdersByUserIdUseCase = getOrdersByUserIdUseCase;
    }

    @PostMapping
    public ResponseEntity<OrderSummaryResponse> place(@RequestBody PlaceOrderRequest request) {
        Order order = placeOrderUseCase.execute(request.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toSummary(order));
    }

    @GetMapping("/{orderId}")
    public OrderSummaryResponse getById(@PathVariable Long orderId) {
        return toSummary(getOrderByIdUseCase.execute(orderId));
    }

    @GetMapping("/user/{userId}")
    public List<OrderSummaryResponse> getByUserId(@PathVariable Long userId) {
        return getOrdersByUserIdUseCase.execute(userId).stream().map(this::toSummary).toList();
    }

    private OrderSummaryResponse toSummary(Order order) {
        return new OrderSummaryResponse(order.getId(), order.getUser().getId(), order.getCreatedAt());
    }

    public record PlaceOrderRequest(Long userId) {
    }

    public record OrderSummaryResponse(Long orderId, Long userId, LocalDateTime createdAt) {
    }
}
