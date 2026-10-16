package com.shop.ecommerce.payment.provider;

import java.math.BigDecimal;

public interface PaymentProvider {

    String getName();

    PaymentSession createPayment(
            Long paymentId,
            Long orderId,
            BigDecimal amount
    );

    record PaymentSession(
            String providerPaymentId,
            String checkoutUrl
    ) {
    }
}