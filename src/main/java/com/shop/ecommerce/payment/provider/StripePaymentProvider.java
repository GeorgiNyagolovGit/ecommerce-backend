package com.shop.ecommerce.payment.provider;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Profile("stripe")
public class StripePaymentProvider implements PaymentProvider {

    @Override
    public String getName() {
        return "STRIPE";
    }

    @Override
    public PaymentSession createPayment(
            Long paymentId,
            Long orderId,
            BigDecimal amount
    ) {
        throw new UnsupportedOperationException(
                "Stripe Checkout integration is not configured"
        );
    }
}