package com.example.springsimplestorev1.application.usecase.product;

import com.example.springsimplestorev1.domain.exception.ResourceNotFoundException;
import com.example.springsimplestorev1.domain.model.Product;
import com.example.springsimplestorev1.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateProductInfoUseCase {

    private final ProductRepository productRepository;

    public UpdateProductInfoUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product execute(Long productId, String name, String description, String imageUrl) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        if (name != null && !name.isBlank()) {
            product.changeName(name);
        }
        if (description != null) {
            product.changeDescription(description);
        }
        if (imageUrl != null) {
            product.changeImageUrl(imageUrl);
        }
        return product;
    }
}
