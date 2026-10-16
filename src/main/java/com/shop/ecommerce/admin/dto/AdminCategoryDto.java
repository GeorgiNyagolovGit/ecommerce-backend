package com.shop.ecommerce.admin.dto;

import java.time.LocalDateTime;

public record AdminCategoryDto(
        Long id,
        String name,
        String slug,
        String description,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
