package com.shop.ecommerce.payment.service;

import com.shop.ecommerce.common.exception.NotFoundException;
import com.shop.ecommerce.order.entity.Order;
import com.shop.ecommerce.order.entity.OrderStatus;
import com.shop.ecommerce.order.repository.OrderRepository;
import com.shop.ecommerce.payment.dto.PaymentRequest;
import com.shop.ecommerce.payment.dto.PaymentResponse;
import com.shop.ecommerce.payment.entity.Payment;
import com.shop.ecommerce.payment.entity.PaymentStatus;
import com.shop.ecommerce.payment.provider.PaymentProvider;
import com.shop.ecommerce.payment.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentProvider paymentProvider;

    public PaymentService(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            PaymentProvider paymentProvider
    ) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.paymentProvider = paymentProvider;
    }

    @Transactional
    public PaymentResponse createPayment(
            Long userId,
            PaymentRequest request
    ) {
        Order order = orderRepository
                .findByIdAndUserId(request.orderId(), userId)
                .orElseThrow(() ->
                        new NotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Only pending orders can be paid"
            );
        }

        Payment existingPayment = paymentRepository
                .findByOrderId(order.getId())
                .orElse(null);

        if (existingPayment != null) {
            if (existingPayment.getStatus() == PaymentStatus.COMPLETED) {
                throw new IllegalArgumentException(
                        "Order has already been paid"
                );
            }

            if (existingPayment.getStatus() == PaymentStatus.PENDING
                    || existingPayment.getStatus() == PaymentStatus.PROCESSING) {
                return toResponse(existingPayment);
            }

            throw new IllegalArgumentException(
                    "A new payment cannot be created for this order"
            );
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setProvider(paymentProvider.getName());

        payment = paymentRepository.save(payment);

        PaymentProvider.PaymentSession session =
                paymentProvider.createPayment(
                        payment.getId(),
                        order.getId(),
                        payment.getAmount()
                );

        payment.setProviderPaymentId(session.providerPaymentId());
        payment.setCheckoutUrl(session.checkoutUrl());

        return toResponse(payment);
    }

    @Transactional
    public PaymentResponse getPayment(Long userId, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new NotFoundException("Payment not found"));

        if (!payment.getOrder().getUser().getId().equals(userId)) {
            throw new NotFoundException("Payment not found");
        }

        return toResponse(payment);
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