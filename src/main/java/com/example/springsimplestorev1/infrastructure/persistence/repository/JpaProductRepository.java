package com.example.springsimplestorev1.infrastructure.persistence.repository;

import com.example.springsimplestorev1.domain.model.Product;
import com.example.springsimplestorev1.domain.repository.ProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProductRepository extends JpaRepository<Product, Long>, ProductRepository {
}
