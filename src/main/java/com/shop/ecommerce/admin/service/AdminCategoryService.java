package com.shop.ecommerce.admin.service;

import com.shop.ecommerce.admin.dto.AdminCategoryDto;
import com.shop.ecommerce.admin.dto.AdminCategoryRequest;
import com.shop.ecommerce.category.entity.Category;
import com.shop.ecommerce.category.repository.CategoryRepository;
import com.shop.ecommerce.common.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminCategoryService {

    private final CategoryRepository categoryRepository;

    public AdminCategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminCategoryDto> getCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminCategoryDto getCategory(Long id) {
        return toDto(findCategory(id));
    }

    @Transactional
    public AdminCategoryDto createCategory(AdminCategoryRequest request) {
        Category category = new Category();
        apply(category, request, true);
        return toDto(categoryRepository.save(category));
    }

    @Transactional
    public AdminCategoryDto updateCategory(
            Long id,
            AdminCategoryRequest request
    ) {
        Category category = findCategory(id);
        apply(category, request, category.isActive());
        return toDto(category);
    }

    @Transactional
    public void deactivateCategory(Long id) {
        findCategory(id).setActive(false);
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Category not found"
                ));
    }

    private void apply(
            Category category,
            AdminCategoryRequest request,
            boolean defaultActive
    ) {
        category.setName(request.name().trim());
        category.setSlug(request.slug().trim().toLowerCase());
        category.setDescription(request.description());
        category.setActive(
                request.active() == null ? defaultActive : request.active()
        );
    }

    private AdminCategoryDto toDto(Category category) {
        return new AdminCategoryDto(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.isActive(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
