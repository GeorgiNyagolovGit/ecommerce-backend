package com.shop.ecommerce.payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;

public record PaymentRequest(
        @NotNull
        @Positive
        @Schema(example = "1")
        Long orderId
) {
}
