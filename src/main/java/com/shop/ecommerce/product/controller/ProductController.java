package com.shop.ecommerce.product.controller;

import com.shop.ecommerce.product.dto.ProductDto;
import com.shop.ecommerce.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Public product catalog")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "List all active products")
    public List<ProductDto> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a product by ID")
    public ProductDto getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "List products in a category")
    public List<ProductDto> getProductsByCategory(
            @PathVariable Long categoryId
    ) {
        return productService.getProductsByCategory(categoryId);
    }

    @GetMapping("/search")
    @Operation(summary = "Search products by keyword")
    public List<ProductDto> searchProducts(
            @RequestParam String keyword
    ) {
        return productService.searchProducts(keyword);
    }
}
