package com.shop.ecommerce.payment.controller;

import com.shop.ecommerce.auth.entity.User;
import com.shop.ecommerce.auth.repository.UserRepository;
import com.shop.ecommerce.common.exception.NotFoundException;
import com.shop.ecommerce.payment.dto.PaymentRequest;
import com.shop.ecommerce.payment.dto.PaymentResponse;
import com.shop.ecommerce.payment.service.PaymentService;
import com.shop.ecommerce.common.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Mock payments", description = "Development-only mock payment flow")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;

    public PaymentController(
            PaymentService paymentService,
            UserRepository userRepository
    ) {
        this.paymentService = paymentService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a mock payment for a pending order")
    public PaymentResponse createPayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PaymentRequest request
    ) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() ->
                        new NotFoundException("User not found"));

        return paymentService.createPayment(user.getId(), request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a mock payment by ID")
    public PaymentResponse getPayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id
    ) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() ->
                        new NotFoundException("User not found"));

        return paymentService.getPayment(user.getId(), id);
    }
}
