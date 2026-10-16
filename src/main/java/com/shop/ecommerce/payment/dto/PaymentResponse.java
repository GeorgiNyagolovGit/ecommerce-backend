package com.shop.ecommerce.payment.dto;

import com.shop.ecommerce.payment.entity.PaymentStatus;

import java.math.BigDecimal;

public record PaymentResponse(
        Long paymentId,
        Long orderId,
        BigDecimal amount,
        PaymentStatus status,
        String provider,
        String checkoutUrl
) {
}

