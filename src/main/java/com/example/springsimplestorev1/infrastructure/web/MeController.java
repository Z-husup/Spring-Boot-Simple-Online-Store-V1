package com.example.springsimplestorev1.infrastructure.web;

import com.example.springsimplestorev1.application.usecase.cart.AddProductToCartUseCase;
import com.example.springsimplestorev1.application.usecase.cart.ClearCartUseCase;
import com.example.springsimplestorev1.application.usecase.cart.GetOrCreateCartUseCase;
import com.example.springsimplestorev1.application.usecase.cart.RemoveProductFromCartUseCase;
import com.example.springsimplestorev1.application.usecase.order.GetOrdersByUserIdUseCase;
import com.example.springsimplestorev1.application.usecase.order.PlaceOrderUseCase;
import com.example.springsimplestorev1.application.usecase.product.GetAllProductsUseCase;
import com.example.springsimplestorev1.application.usecase.user.GetUserByEmailUseCase;
import com.example.springsimplestorev1.domain.model.Cart;
import com.example.springsimplestorev1.domain.model.CartItem;
import com.example.springsimplestorev1.domain.model.Order;
import com.example.springsimplestorev1.domain.model.Product;
import com.example.springsimplestorev1.domain.model.User;
import com.example.springsimplestorev1.domain.repository.CartItemRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/me")
@PreAuthorize("hasAnyRole('CLIENT','ADMIN')")
public class MeController {

    private final GetUserByEmailUseCase getUserByEmailUseCase;
    private final GetAllProductsUseCase getAllProductsUseCase;
    private final GetOrCreateCartUseCase getOrCreateCartUseCase;
    private final AddProductToCartUseCase addProductToCartUseCase;
    private final RemoveProductFromCartUseCase removeProductFromCartUseCase;
    private final ClearCartUseCase clearCartUseCase;
    private final CartItemRepository cartItemRepository;
    private final PlaceOrderUseCase placeOrderUseCase;
    private final GetOrdersByUserIdUseCase getOrdersByUserIdUseCase;

    public MeController(
            GetUserByEmailUseCase getUserByEmailUseCase,
            GetAllProductsUseCase getAllProductsUseCase,
            GetOrCreateCartUseCase getOrCreateCartUseCase,
            AddProductToCartUseCase addProductToCartUseCase,
            RemoveProductFromCartUseCase removeProductFromCartUseCase,
            ClearCartUseCase clearCartUseCase,
            CartItemRepository cartItemRepository,
            PlaceOrderUseCase placeOrderUseCase,
            GetOrdersByUserIdUseCase getOrdersByUserIdUseCase
    ) {
        this.getUserByEmailUseCase = getUserByEmailUseCase;
        this.getAllProductsUseCase = getAllProductsUseCase;
        this.getOrCreateCartUseCase = getOrCreateCartUseCase;
        this.addProductToCartUseCase = addProductToCartUseCase;
        this.removeProductFromCartUseCase = removeProductFromCartUseCase;
        this.clearCartUseCase = clearCartUseCase;
        this.cartItemRepository = cartItemRepository;
        this.placeOrderUseCase = placeOrderUseCase;
        this.getOrdersByUserIdUseCase = getOrdersByUserIdUseCase;
    }

    @GetMapping("/profile")
    public ProfileResponse profile(Authentication authentication) {
        User user = currentUser(authentication);
        return new ProfileResponse(user.getId(), user.getEmail(), user.getName());
    }

    @GetMapping("/products")
    public List<ProductResponse> products() {
        return getAllProductsUseCase.execute().stream().map(this::toProductResponse).toList();
    }

    @GetMapping("/cart")
    public CartResponse cart(Authentication authentication) {
        User user = currentUser(authentication);
        Cart cart = getOrCreateCartUseCase.execute(user.getId());
        return toCartResponse(cart, user.getId());
    }

    @PostMapping("/cart/items")
    public CartResponse addToCart(Authentication authentication, @RequestBody AddCartItemRequest request) {
        User user = currentUser(authentication);
        Cart cart = addProductToCartUseCase.execute(user.getId(), request.productId(), request.quantity());
        return toCartResponse(cart, user.getId());
    }

    @DeleteMapping("/cart/items/{productId}")
    public CartResponse removeFromCart(Authentication authentication, @PathVariable Long productId) {
        User user = currentUser(authentication);
        Cart cart = removeProductFromCartUseCase.execute(user.getId(), productId);
        return toCartResponse(cart, user.getId());
    }

    @DeleteMapping("/cart")
    public CartResponse clearCart(Authentication authentication) {
        User user = currentUser(authentication);
        Cart cart = clearCartUseCase.execute(user.getId());
        return toCartResponse(cart, user.getId());
    }

    @PostMapping("/orders")
    public OrderResponse checkout(Authentication authentication) {
        User user = currentUser(authentication);
        Order order = placeOrderUseCase.execute(user.getId());
        return toOrderResponse(order);
    }

    @GetMapping("/orders")
    public List<OrderResponse> orders(Authentication authentication) {
        User user = currentUser(authentication);
        return getOrdersByUserIdUseCase.execute(user.getId()).stream().map(this::toOrderResponse).toList();
    }

    private User currentUser(Authentication authentication) {
        return getUserByEmailUseCase.execute(authentication.getName());
    }

    private CartResponse toCartResponse(Cart cart, Long userId) {
        List<CartItemResponse> items = cartItemRepository.findByCartId(cart.getId()).stream()
                .map(item -> new CartItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getProduct().getPrice(),
                        item.getQuantity(),
                        item.getProduct().getPrice() * item.getQuantity()
                ))
                .toList();

        double total = items.stream().mapToDouble(CartItemResponse::lineTotal).sum();
        return new CartResponse(cart.getId(), userId, items, total);
    }

    private ProductResponse toProductResponse(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getDescription(), p.getImageUrl(), p.getPrice(), p.getStock());
    }

    private OrderResponse toOrderResponse(Order order) {
        return new OrderResponse(order.getId(), order.getCreatedAt(), order.getTotal());
    }

    public record ProfileResponse(Long id, String email, String name) {}

    public record ProductResponse(Long id, String name, String description, String imageUrl, double price, int stock) {}

    public record AddCartItemRequest(Long productId, int quantity) {}

    public record CartItemResponse(Long productId, String productName, double price, int quantity, double lineTotal) {}

    public record CartResponse(Long cartId, Long userId, List<CartItemResponse> items, double total) {}

    public record OrderResponse(Long orderId, LocalDateTime createdAt, double total) {}
}
