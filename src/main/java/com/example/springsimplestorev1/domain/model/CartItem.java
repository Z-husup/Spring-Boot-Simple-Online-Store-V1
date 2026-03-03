package com.example.springsimplestorev1.domain.model;

import com.example.springsimplestorev1.domain.exception.BusinessRuleException;
import com.example.springsimplestorev1.domain.exception.ValidationException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private int quantity;

    protected CartItem(Cart cart, Product product, int quantity) {
        if (quantity <= 0) {
            throw new ValidationException("Quantity must be positive");
        }

        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    }

    void increaseQuantity(int amount) {
        if (amount <= 0) {
            throw new ValidationException("Amount must be positive");
        }
        this.quantity += amount;
    }

    void decreaseQuantity(int amount) {
        if (amount <= 0) {
            throw new ValidationException("Amount must be positive");
        }
        if (this.quantity - amount <= 0) {
            throw new BusinessRuleException("Quantity cannot go below 1");
        }
        this.quantity -= amount;
    }

    double getTotal() {
        return product.getPrice() * quantity;
    }
}
