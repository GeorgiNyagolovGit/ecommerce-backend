package com.shop.ecommerce.payment.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments/webhook")
@Hidden
public class PaymentWebhookController {

    @PostMapping
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public void handleWebhook() {
        throw new UnsupportedOperationException(
                "Payment webhook is not configured"
        );
    }
}
