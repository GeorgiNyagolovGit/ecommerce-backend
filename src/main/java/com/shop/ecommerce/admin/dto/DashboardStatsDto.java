package com.shop.ecommerce.admin.dto;

import java.math.BigDecimal;

public record DashboardStatsDto(
        long users,
        long products,
        long orders,
        long pendingOrders,
        long completedPayments,
        BigDecimal totalRevenue
) {
}
