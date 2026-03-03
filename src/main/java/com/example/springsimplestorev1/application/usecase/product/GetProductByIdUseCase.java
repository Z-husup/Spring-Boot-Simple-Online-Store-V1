package com.example.springsimplestorev1.application.usecase.product;

import com.example.springsimplestorev1.domain.exception.ResourceNotFoundException;
import com.example.springsimplestorev1.domain.model.Product;
import com.example.springsimplestorev1.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class GetProductByIdUseCase {

    private final ProductRepository productRepository;

    public GetProductByIdUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product execute(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
    }
}