package com.shop.ecommerce.admin.controller;

import com.shop.ecommerce.admin.dto.AdminProductDto;
import com.shop.ecommerce.admin.dto.AdminProductRequest;
import com.shop.ecommerce.admin.service.AdminProductService;
import com.shop.ecommerce.common.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@Tag(name = "Admin products")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class AdminProductController {

    private final AdminProductService productService;

    public AdminProductController(AdminProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "List all products, including inactive ones")
    public List<AdminProductDto> getProducts() {
        return productService.getProducts();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a product by ID")
    public AdminProductDto getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a product")
    public AdminProductDto createProduct(
            @Valid @RequestBody AdminProductRequest request
    ) {
        return productService.createProduct(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product")
    public AdminProductDto updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody AdminProductRequest request
    ) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deactivate a product")
    public void deactivateProduct(@PathVariable Long id) {
        productService.deactivateProduct(id);
    }
}
