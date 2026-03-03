package com.example.springsimplestorev1.infrastructure.web;

import com.example.springsimplestorev1.application.usecase.product.ChangeProductPriceUseCase;
import com.example.springsimplestorev1.application.usecase.product.CreateProductUseCase;
import com.example.springsimplestorev1.application.usecase.product.GetAllProductsUseCase;
import com.example.springsimplestorev1.application.usecase.product.GetProductByIdUseCase;
import com.example.springsimplestorev1.application.usecase.product.RestockProductUseCase;
import com.example.springsimplestorev1.application.usecase.product.SearchProductsByNameUseCase;
import com.example.springsimplestorev1.application.usecase.product.UpdateProductInfoUseCase;
import com.example.springsimplestorev1.domain.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductInfoUseCase updateProductInfoUseCase;
    private final ChangeProductPriceUseCase changeProductPriceUseCase;
    private final RestockProductUseCase restockProductUseCase;
    private final GetProductByIdUseCase getProductByIdUseCase;
    private final GetAllProductsUseCase getAllProductsUseCase;
    private final SearchProductsByNameUseCase searchProductsByNameUseCase;

    public ProductController(
            CreateProductUseCase createProductUseCase,
            UpdateProductInfoUseCase updateProductInfoUseCase,
            ChangeProductPriceUseCase changeProductPriceUseCase,
            RestockProductUseCase restockProductUseCase,
            GetProductByIdUseCase getProductByIdUseCase,
            GetAllProductsUseCase getAllProductsUseCase,
            SearchProductsByNameUseCase searchProductsByNameUseCase
    ) {
        this.createProductUseCase = createProductUseCase;
        this.updateProductInfoUseCase = updateProductInfoUseCase;
        this.changeProductPriceUseCase = changeProductPriceUseCase;
        this.restockProductUseCase = restockProductUseCase;
        this.getProductByIdUseCase = getProductByIdUseCase;
        this.getAllProductsUseCase = getAllProductsUseCase;
        this.searchProductsByNameUseCase = searchProductsByNameUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> create(@RequestBody CreateProductRequest request) {
        Product product = createProductUseCase.execute(
                request.name(),
                request.description(),
                request.imageUrl(),
                request.price(),
                request.stock()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(product));
    }

    @PatchMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse updateInfo(@PathVariable Long productId, @RequestBody UpdateProductInfoRequest request) {
        return toResponse(updateProductInfoUseCase.execute(
                productId,
                request.name(),
                request.description(),
                request.imageUrl()
        ));
    }

    @PatchMapping("/{productId}/price")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse changePrice(@PathVariable Long productId, @RequestBody ChangePriceRequest request) {
        return toResponse(changeProductPriceUseCase.execute(productId, request.price()));
    }

    @PatchMapping("/{productId}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse restock(@PathVariable Long productId, @RequestBody RestockProductRequest request) {
        return toResponse(restockProductUseCase.execute(productId, request.quantity()));
    }

    @GetMapping("/{productId}")
    @PreAuthorize("hasAnyRole('CLIENT','ADMIN')")
    public ProductResponse getById(@PathVariable Long productId) {
        return toResponse(getProductByIdUseCase.execute(productId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENT','ADMIN')")
    public List<ProductResponse> getAllOrSearch(@RequestParam(required = false) String name) {
        List<Product> products = (name == null || name.isBlank())
                ? getAllProductsUseCase.execute()
                : searchProductsByNameUseCase.execute(name);
        return products.stream().map(this::toResponse).toList();
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getImageUrl(),
                product.getPrice(),
                product.getStock()
        );
    }

    public record CreateProductRequest(String name, String description, String imageUrl, double price, int stock) {
    }

    public record UpdateProductInfoRequest(String name, String description, String imageUrl) {
    }

    public record ChangePriceRequest(double price) {
    }

    public record RestockProductRequest(int quantity) {
    }

    public record ProductResponse(Long id, String name, String description, String imageUrl, double price, int stock) {
    }
}
