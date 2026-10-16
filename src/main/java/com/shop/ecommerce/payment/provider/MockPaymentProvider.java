package com.shop.ecommerce.payment.provider;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@Profile("mock-payment")
public class MockPaymentProvider implements PaymentProvider {

    @Override
    public String getName() {
        return "MOCK";
    }

    @Override
    public PaymentSession createPayment(
            Long paymentId,
            Long orderId,
            BigDecimal amount
    ) {
        String providerPaymentId = UUID.randomUUID().toString();

        return new PaymentSession(
                providerPaymentId,
                "http://localhost:4200/payment/mock/" + paymentId
        );
    }
}