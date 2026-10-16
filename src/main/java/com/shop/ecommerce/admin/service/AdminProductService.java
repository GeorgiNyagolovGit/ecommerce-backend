package com.shop.ecommerce.admin.service;

import com.shop.ecommerce.admin.dto.AdminProductDto;
import com.shop.ecommerce.admin.dto.AdminProductRequest;
import com.shop.ecommerce.category.entity.Category;
import com.shop.ecommerce.category.repository.CategoryRepository;
import com.shop.ecommerce.common.exception.NotFoundException;
import com.shop.ecommerce.product.entity.Product;
import com.shop.ecommerce.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public AdminProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminProductDto> getProducts() {
        return productRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminProductDto getProduct(Long id) {
        return toDto(findProduct(id));
    }

    @Transactional
    public AdminProductDto createProduct(AdminProductRequest request) {
        Product product = new Product();
        apply(product, request, true);
        return toDto(productRepository.save(product));
    }

    @Transactional
    public AdminProductDto updateProduct(
            Long id,
            AdminProductRequest request
    ) {
        Product product = findProduct(id);
        apply(product, request, product.isActive());
        return toDto(product);
    }

    @Transactional
    public void deactivateProduct(Long id) {
        findProduct(id).setActive(false);
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Product not found"
                ));
    }

    private void apply(
            Product product,
            AdminProductRequest request,
            boolean defaultActive
    ) {
        Category category = categoryRepository
                .findByIdAndActiveTrue(request.categoryId())
                .orElseThrow(() -> new NotFoundException(
                        "Category not found"
                ));

        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setImageUrl(request.imageUrl());
        product.setCategory(category);
        product.setActive(
                request.active() == null ? defaultActive : request.active()
        );
    }

    private AdminProductDto toDto(Product product) {
        return new AdminProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getImageUrl(),
                product.isActive(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
