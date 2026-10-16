package com.shop.ecommerce.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AdminProductRequest(
        @NotBlank
        @Size(max = 150)
        @Schema(example = "Java Programming Book")
        String name,

        @Size(max = 2000)
        @Schema(example = "A practical Java guide")
        String description,

        @NotNull
        @DecimalMin(value = "0.01")
        @Schema(example = "49.90")
        BigDecimal price,

        @NotNull
        @Min(0)
        @Schema(example = "25")
        Integer stock,

        @Size(max = 500)
        @Schema(example = "https://example.com/java-book.jpg")
        String imageUrl,

        @NotNull
        @Schema(example = "1")
        Long categoryId,

        @Schema(example = "true")
        Boolean active
) {
}
