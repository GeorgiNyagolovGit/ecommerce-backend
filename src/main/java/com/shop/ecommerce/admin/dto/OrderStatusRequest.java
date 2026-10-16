package com.shop.ecommerce.admin.dto;

import com.shop.ecommerce.order.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record OrderStatusRequest(
        @NotNull
        @Schema(example = "SHIPPED")
        OrderStatus status
) {
}
