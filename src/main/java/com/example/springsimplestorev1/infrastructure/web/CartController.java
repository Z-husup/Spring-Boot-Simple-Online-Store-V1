package com.example.springsimplestorev1.infrastructure.web;

import com.example.springsimplestorev1.application.usecase.cart.AddProductToCartUseCase;
import com.example.springsimplestorev1.application.usecase.cart.ClearCartUseCase;
import com.example.springsimplestorev1.application.usecase.cart.GetCartByUserIdUseCase;
import com.example.springsimplestorev1.application.usecase.cart.GetCartTotalUseCase;
import com.example.springsimplestorev1.application.usecase.cart.GetOrCreateCartUseCase;
import com.example.springsimplestorev1.application.usecase.cart.RemoveProductFromCartUseCase;
import com.example.springsimplestorev1.domain.model.Cart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/client/carts")
@PreAuthorize("hasAnyRole('CLIENT','ADMIN')")
public class CartController {

    private final GetOrCreateCartUseCase getOrCreateCartUseCase;
    private final GetCartByUserIdUseCase getCartByUserIdUseCase;
    private final AddProductToCartUseCase addProductToCartUseCase;
    private final RemoveProductFromCartUseCase removeProductFromCartUseCase;
    private final ClearCartUseCase clearCartUseCase;
    private final GetCartTotalUseCase getCartTotalUseCase;

    public CartController(
            GetOrCreateCartUseCase getOrCreateCartUseCase,
            GetCartByUserIdUseCase getCartByUserIdUseCase,
            AddProductToCartUseCase addProductToCartUseCase,
            RemoveProductFromCartUseCase removeProductFromCartUseCase,
            ClearCartUseCase clearCartUseCase,
            GetCartTotalUseCase getCartTotalUseCase
    ) {
        this.getOrCreateCartUseCase = getOrCreateCartUseCase;
        this.getCartByUserIdUseCase = getCartByUserIdUseCase;
        this.addProductToCartUseCase = addProductToCartUseCase;
        this.removeProductFromCartUseCase = removeProductFromCartUseCase;
        this.clearCartUseCase = clearCartUseCase;
        this.getCartTotalUseCase = getCartTotalUseCase;
    }

    @PostMapping("/{userId}")
    public CartSummaryResponse getOrCreate(@PathVariable Long userId) {
        return toSummary(getOrCreateCartUseCase.execute(userId), getCartTotalUseCase.execute(userId));
    }

    @GetMapping("/{userId}")
    public CartSummaryResponse getByUserId(@PathVariable Long userId) {
        return toSummary(getCartByUserIdUseCase.execute(userId), getCartTotalUseCase.execute(userId));
    }

    @PostMapping("/{userId}/items")
    public CartSummaryResponse addItem(@PathVariable Long userId, @RequestBody AddCartItemRequest request) {
        Cart cart = addProductToCartUseCase.execute(userId, request.productId(), request.quantity());
        return toSummary(cart, getCartTotalUseCase.execute(userId));
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public CartSummaryResponse removeItem(@PathVariable Long userId, @PathVariable Long productId) {
        Cart cart = removeProductFromCartUseCase.execute(userId, productId);
        return toSummary(cart, getCartTotalUseCase.execute(userId));
    }

    @DeleteMapping("/{userId}")
    public CartSummaryResponse clear(@PathVariable Long userId) {
        Cart cart = clearCartUseCase.execute(userId);
        return toSummary(cart, getCartTotalUseCase.execute(userId));
    }

    @GetMapping("/{userId}/total")
    public CartTotalResponse getTotal(@PathVariable Long userId) {
        return new CartTotalResponse(userId, getCartTotalUseCase.execute(userId));
    }

    private CartSummaryResponse toSummary(Cart cart, double total) {
        return new CartSummaryResponse(cart.getId(), cart.getUser().getId(), total);
    }

    public record AddCartItemRequest(Long productId, int quantity) {
    }

    public record CartSummaryResponse(Long cartId, Long userId, double total) {
    }

    public record CartTotalResponse(Long userId, double total) {
    }
}
