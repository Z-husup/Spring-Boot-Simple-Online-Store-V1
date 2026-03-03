package com.example.springsimplestorev1.application.usecase.product;

import com.example.springsimplestorev1.domain.model.Product;
import com.example.springsimplestorev1.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateProductUseCase {

    private final ProductRepository productRepository;

    public CreateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product execute(String name, String description, String imageUrl, double price, int stock) {
        return productRepository.save(new Product(name, description, imageUrl, price, stock));
    }
}
