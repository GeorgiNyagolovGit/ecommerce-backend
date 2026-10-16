package com.shop.ecommerce.admin.controller;

import com.shop.ecommerce.admin.dto.PaymentStatusRequest;
import com.shop.ecommerce.admin.service.AdminPaymentService;
import com.shop.ecommerce.common.config.OpenApiConfig;
import com.shop.ecommerce.payment.dto.PaymentResponse;
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
@RequestMapping("/api/admin/payments")
@Tag(name = "Admin payments")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class AdminPaymentController {

    private final AdminPaymentService paymentService;

    public AdminPaymentController(AdminPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    @Operation(summary = "List all payments")
    public List<PaymentResponse> getPayments() {
        return paymentService.getPayments();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a payment by ID")
    public PaymentResponse getPayment(@PathVariable Long id) {
        return paymentService.getPayment(id);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update a mock payment status")
    public PaymentResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody PaymentStatusRequest request
    ) {
        return paymentService.updateStatus(id, request.status());
    }
}
