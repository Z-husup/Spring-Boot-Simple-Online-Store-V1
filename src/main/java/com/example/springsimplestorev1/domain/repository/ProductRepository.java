package com.example.springsimplestorev1.domain.repository;

import com.example.springsimplestorev1.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(Long id);

    List<Product> findAll();

    List<Product> findByNameContainingIgnoreCase(String name);
}
