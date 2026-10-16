package com.shop.ecommerce.admin;

import com.shop.ecommerce.admin.service.AdminPaymentService;
import com.shop.ecommerce.order.entity.Order;
import com.shop.ecommerce.order.entity.OrderStatus;
import com.shop.ecommerce.payment.dto.PaymentResponse;
import com.shop.ecommerce.payment.entity.Payment;
import com.shop.ecommerce.payment.entity.PaymentStatus;
import com.shop.ecommerce.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminPaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    private AdminPaymentService paymentService;
    private Payment payment;

    @BeforeEach
    void setUp() {
        paymentService = new AdminPaymentService(paymentRepository);

        Order order = new Order();
        order.setId(10L);
        order.setStatus(OrderStatus.PENDING);

        payment = new Payment();
        payment.setId(20L);
        payment.setOrder(order);
        payment.setAmount(new BigDecimal("99.80"));
        payment.setStatus(PaymentStatus.PENDING);
        payment.setProvider("MOCK");
    }

    @Test
    void completingPaymentMarksOrderPaid() {
        when(paymentRepository.findById(20L))
                .thenReturn(Optional.of(payment));

        PaymentResponse result = paymentService.updateStatus(
                20L,
                PaymentStatus.COMPLETED
        );

        assertEquals(PaymentStatus.COMPLETED, result.status());
        assertEquals(OrderStatus.PAID, payment.getOrder().getStatus());
    }
}
