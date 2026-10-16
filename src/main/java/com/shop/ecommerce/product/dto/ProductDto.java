package com.shop.ecommerce.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private Integer stock;
        private String imageUrl;
        private Long categoryId;
        private String categoryName;
        private LocalDateTime createdAt;
}