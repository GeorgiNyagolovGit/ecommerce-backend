package com.shop.ecommerce.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminCategoryRequest(
        @NotBlank
        @Size(max = 100)
        @Schema(example = "Books")
        String name,

        @NotBlank
        @Size(max = 120)
        @Schema(example = "books")
        String slug,

        @Size(max = 500)
        @Schema(example = "Programming and technology books")
        String description,

        @Schema(example = "true")
        Boolean active
) {
}
