package com.example.springsimplestorev1.domain.model;

import com.example.springsimplestorev1.domain.exception.InsufficientStockException;
import com.example.springsimplestorev1.domain.exception.ValidationException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String imageUrl;
    private double price;
    private int stock;

    public Product(String name, String description, double price, int stock) {
        this(name, description, null, price, stock);
    }

    public Product(String name, String description, String imageUrl, double price, int stock) {
        if (price < 0) {
            throw new ValidationException("Price cannot be negative");
        }
        if (stock < 0) {
            throw new ValidationException("Stock cannot be negative");
        }

        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.stock = stock;
    }

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new ValidationException("Quantity must be positive");
        }
        if (stock < quantity) {
            throw new InsufficientStockException("Not enough stock");
        }
        this.stock -= quantity;
    }

    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new ValidationException("Quantity must be positive");
        }
        this.stock += quantity;
    }

    public void changePrice(double newPrice) {
        if (newPrice < 0) {
            throw new ValidationException("Price cannot be negative");
        }
        this.price = newPrice;
    }

    public void changeName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new ValidationException("Name cannot be blank");
        }
        this.name = newName;
    }

    public void changeDescription(String newDescription) {
        this.description = newDescription;
    }

    public void changeImageUrl(String newImageUrl) {
        this.imageUrl = newImageUrl;
    }
}
