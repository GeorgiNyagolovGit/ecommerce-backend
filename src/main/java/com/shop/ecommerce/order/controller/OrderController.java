package com.shop.ecommerce.order.controller;

import com.shop.ecommerce.order.dto.OrderDto;
import com.shop.ecommerce.order.service.OrderService;
import com.shop.ecommerce.common.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Validated
@Tag(name = "Orders", description = "Authenticated customer orders")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create an order")
    public OrderDto createOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        return orderService.createOrder(
                userDetails.getUsername(),
                request.getShippingAddress(),
                request.getItems().stream()
                        .map(item -> new OrderDto.OrderItemDto(
                                item.getProductId(),
                                null,
                                item.getQuantity(),
                                null,
                                null
                        ))
                        .toList()
        );
    }

    @GetMapping
    @Operation(summary = "List the current user's orders")
    public List<OrderDto> getMyOrders(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return orderService.getMyOrders(userDetails.getUsername());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one of the current user's orders")
    public OrderDto getMyOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id
    ) {
        return orderService.getMyOrder(userDetails.getUsername(), id);
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel a pending order")
    public OrderDto cancelOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id
    ) {
        return orderService.cancelOrder(userDetails.getUsername(), id);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateOrderRequest {

        @NotBlank
        @Size(max = 255)
        @Schema(example = "10 Bulgaria Blvd, Sofia")
        private String shippingAddress;

        @NotEmpty
        @Valid
        private List<CreateOrderItemRequest> items;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateOrderItemRequest {

        @NotNull
        @Schema(example = "1")
        private Long productId;

        @NotNull
        @Min(1)
        @Schema(example = "2", minimum = "1")
        private Integer quantity;
    }
}
