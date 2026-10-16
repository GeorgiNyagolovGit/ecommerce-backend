package com.shop.ecommerce.payment.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@Profile("mock-payment")
public class MockPaymentProvider implements PaymentProvider {

    private final String frontendUrl;

    public MockPaymentProvider(
            @Value("${app.frontend-url}") String frontendUrl
    ) {
        this.frontendUrl = frontendUrl.replaceAll("/+$", "");
    }

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
                frontendUrl + "/payment/mock/" + paymentId
        );
    }
}
