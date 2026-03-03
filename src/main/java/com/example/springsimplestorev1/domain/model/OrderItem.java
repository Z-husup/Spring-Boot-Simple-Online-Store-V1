package com.example.springsimplestorev1.domain.model;

import com.example.springsimplestorev1.domain.exception.ValidationException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private int quantity;
    private double priceAtPurchase;

    public OrderItem(Product product, int quantity) {
        if (quantity <= 0) {
            throw new ValidationException("Quantity must be positive");
        }

        this.product = product;
        this.quantity = quantity;
        this.priceAtPurchase = product.getPrice();
    }

    void attachToOrder(Order order) {
        this.order = order;
    }

    public double getTotal() {
        return priceAtPurchase * quantity;
    }
}
