package com.example.springsimplestorev1.application.usecase.cart;

import com.example.springsimplestorev1.domain.exception.InsufficientStockException;
import com.example.springsimplestorev1.domain.exception.ResourceNotFoundException;
import com.example.springsimplestorev1.domain.model.Cart;
import com.example.springsimplestorev1.domain.model.CartItem;
import com.example.springsimplestorev1.domain.model.Product;
import com.example.springsimplestorev1.domain.model.User;
import com.example.springsimplestorev1.domain.repository.CartRepository;
import com.example.springsimplestorev1.domain.repository.ProductRepository;
import com.example.springsimplestorev1.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AddProductToCartUseCase {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public AddProductToCartUseCase(CartRepository cartRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Cart execute(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
                    return cartRepository.save(new Cart(user));
                });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        int existingQuantity = getExistingItemQuantity(cart, productId);
        int requestedTotal = existingQuantity + quantity;
        if (requestedTotal > product.getStock()) {
            throw new InsufficientStockException("Requested quantity exceeds available stock");
        }

        cart.addProduct(product, quantity);
        return cart;
    }

    private int getExistingItemQuantity(Cart cart, Long productId) {
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        return existingItem.map(CartItem::getQuantity).orElse(0);
    }
}