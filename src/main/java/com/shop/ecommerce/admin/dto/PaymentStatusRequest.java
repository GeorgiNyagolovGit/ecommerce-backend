package com.shop.ecommerce.admin.dto;

import com.shop.ecommerce.payment.entity.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record PaymentStatusRequest(
        @NotNull
        @Schema(example = "COMPLETED")
        PaymentStatus status
) {
}
