package com.shop.ecommerce.admin.controller;

import com.shop.ecommerce.admin.dto.AdminCategoryDto;
import com.shop.ecommerce.admin.dto.AdminCategoryRequest;
import com.shop.ecommerce.admin.service.AdminCategoryService;
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
@RequestMapping("/api/admin/categories")
@Tag(name = "Admin categories")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class AdminCategoryController {

    private final AdminCategoryService categoryService;

    public AdminCategoryController(AdminCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "List all categories, including inactive ones")
    public List<AdminCategoryDto> getCategories() {
        return categoryService.getCategories();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a category by ID")
    public AdminCategoryDto getCategory(@PathVariable Long id) {
        return categoryService.getCategory(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a category")
    public AdminCategoryDto createCategory(
            @Valid @RequestBody AdminCategoryRequest request
    ) {
        return categoryService.createCategory(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a category")
    public AdminCategoryDto updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody AdminCategoryRequest request
    ) {
        return categoryService.updateCategory(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deactivate a category")
    public void deactivateCategory(@PathVariable Long id) {
        categoryService.deactivateCategory(id);
    }
}
