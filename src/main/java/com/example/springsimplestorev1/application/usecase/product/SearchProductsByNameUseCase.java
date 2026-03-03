package com.example.springsimplestorev1.application.usecase.product;

import com.example.springsimplestorev1.domain.model.Product;
import com.example.springsimplestorev1.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchProductsByNameUseCase {

    private final ProductRepository productRepository;

    public SearchProductsByNameUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> execute(String name) {
        if (name == null || name.isBlank()) {
            return productRepository.findAll();
        }
        return productRepository.findByNameContainingIgnoreCase(name);
    }
}