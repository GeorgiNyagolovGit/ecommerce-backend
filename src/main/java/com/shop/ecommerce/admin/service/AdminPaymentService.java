package com.shop.ecommerce.admin.service;

import com.shop.ecommerce.common.exception.NotFoundException;
import com.shop.ecommerce.order.entity.OrderStatus;
import com.shop.ecommerce.payment.dto.PaymentResponse;
import com.shop.ecommerce.payment.entity.Payment;
import com.shop.ecommerce.payment.entity.PaymentStatus;
import com.shop.ecommerce.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AdminPaymentService {

    private static final Map<PaymentStatus, Set<PaymentStatus>> ALLOWED_TRANSITIONS =
            Map.of(
                    PaymentStatus.PENDING,
                    Set.of(
                            PaymentStatus.PROCESSING,
                            PaymentStatus.COMPLETED,
                            PaymentStatus.FAILED,
                            PaymentStatus.CANCELLED
                    ),
                    PaymentStatus.PROCESSING,
                    Set.of(PaymentStatus.COMPLETED, PaymentStatus.FAILED),
                    PaymentStatus.COMPLETED,
                    Set.of(PaymentStatus.REFUNDED),
                    PaymentStatus.FAILED,
                    Set.of(),
                    PaymentStatus.REFUNDED,
                    Set.of(),
                    PaymentStatus.CANCELLED,
                    Set.of()
            );

    private final PaymentRepository paymentRepository;

    public AdminPaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPayments() {
        return paymentRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long id) {
        return toResponse(findPayment(id));
    }

    @Transactional
    public PaymentResponse updateStatus(Long id, PaymentStatus newStatus) {
        Payment payment = findPayment(id);
        PaymentStatus currentStatus = payment.getStatus();

        if (currentStatus == newStatus) {
            return toResponse(payment);
        }

        if (!ALLOWED_TRANSITIONS
                .getOrDefault(currentStatus, Set.of())
                .contains(newStatus)) {
            throw new IllegalArgumentException(
                    "Invalid payment status transition from "
                            + currentStatus + " to " + newStatus
            );
        }

        payment.setStatus(newStatus);
        if (newStatus == PaymentStatus.COMPLETED
                && payment.getOrder().getStatus() == OrderStatus.PENDING) {
            payment.getOrder().setStatus(OrderStatus.PAID);
        }

        return toResponse(payment);
    }

    private Payment findPayment(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Payment not found"
                ));
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getProvider(),
                payment.getCheckoutUrl()
        );
    }
}
