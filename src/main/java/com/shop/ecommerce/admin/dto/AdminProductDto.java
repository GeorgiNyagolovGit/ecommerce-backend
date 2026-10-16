package com.shop.ecommerce.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AdminProductDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        String imageUrl,
        boolean active,
        Long categoryId,
        String categoryName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
