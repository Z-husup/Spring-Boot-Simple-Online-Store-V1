package com.example.springsimplestorev1.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    private LocalDateTime createdAt;

    public Order(User user) {
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }

    public void addItem(OrderItem item) {
        item.attachToOrder(this);
        items.add(item);
    }

    public double getTotal() {
        return items.stream()
                .mapToDouble(OrderItem::getTotal)
                .sum();
    }
}
