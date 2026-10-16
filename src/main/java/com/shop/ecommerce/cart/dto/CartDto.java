package com.shop.ecommerce.cart.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartDto(
        Long id,
        Long userId,
        List<CartItemDto> items,
        BigDecimal total
) {

    public record CartItemDto(
            Long id,
            Long productId,
            String productName,
            BigDecimal price,
            String imageUrl,
            Integer quantity,
            BigDecimal subtotal
    ) {
    }
}