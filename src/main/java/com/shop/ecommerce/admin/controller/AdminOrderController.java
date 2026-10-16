package com.shop.ecommerce.admin.controller;

import com.shop.ecommerce.admin.dto.OrderStatusRequest;
import com.shop.ecommerce.admin.service.AdminOrderService;
import com.shop.ecommerce.common.config.OpenApiConfig;
import com.shop.ecommerce.order.dto.OrderDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@Tag(name = "Admin orders")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class AdminOrderController {

    private final AdminOrderService orderService;

    public AdminOrderController(AdminOrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @Operation(summary = "List all orders")
    public List<OrderDto> getOrders() {
        return orderService.getOrders();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an order by ID")
    public OrderDto getOrder(@PathVariable Long id) {
        return orderService.getOrder(id);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update an order status")
    public OrderDto updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusRequest request
    ) {
        return orderService.updateStatus(id, request.status());
    }
}
