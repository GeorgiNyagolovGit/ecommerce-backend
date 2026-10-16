package com.shop.ecommerce.category.service;

import com.shop.ecommerce.category.dto.CategoryDto;
import com.shop.ecommerce.category.entity.Category;
import com.shop.ecommerce.category.repository.CategoryRepository;
import com.shop.ecommerce.common.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        return toDto(category);
    }

    private CategoryDto toDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getCreatedAt()
        );
    }
}