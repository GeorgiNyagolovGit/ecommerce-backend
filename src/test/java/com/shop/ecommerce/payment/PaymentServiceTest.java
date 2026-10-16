package com.shop.ecommerce.payment;

import com.shop.ecommerce.auth.entity.User;
import com.shop.ecommerce.order.entity.Order;
import com.shop.ecommerce.order.entity.OrderStatus;
import com.shop.ecommerce.order.repository.OrderRepository;
import com.shop.ecommerce.payment.dto.PaymentRequest;
import com.shop.ecommerce.payment.dto.PaymentResponse;
import com.shop.ecommerce.payment.entity.Payment;
import com.shop.ecommerce.payment.entity.PaymentStatus;
import com.shop.ecommerce.payment.provider.PaymentProvider;
import com.shop.ecommerce.payment.repository.PaymentRepository;
import com.shop.ecommerce.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentProvider paymentProvider;

    private PaymentService paymentService;
    private Order pendingOrder;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(
                paymentRepository,
                orderRepository,
                paymentProvider
        );

        User user = new User();
        user.setId(7L);

        pendingOrder = new Order();
        pendingOrder.setId(10L);
        pendingOrder.setUser(user);
        pendingOrder.setStatus(OrderStatus.PENDING);
        pendingOrder.setTotalAmount(new BigDecimal("99.80"));
    }

    @Test
    void createPaymentReturnsMockCheckoutSession() {
        when(orderRepository.findByIdAndUserId(10L, 7L))
                .thenReturn(Optional.of(pendingOrder));
        when(paymentRepository.findByOrderId(10L))
                .thenReturn(Optional.empty());
        when(paymentProvider.getName()).thenReturn("MOCK");
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> {
                    Payment payment = invocation.getArgument(0);
                    payment.setId(20L);
                    return payment;
                });
        when(paymentProvider.createPayment(
                20L,
                10L,
                new BigDecimal("99.80")
        )).thenReturn(new PaymentProvider.PaymentSession(
                "mock-session",
                "http://localhost:4200/payment/mock/20"
        ));

        PaymentResponse result = paymentService.createPayment(
                7L,
                new PaymentRequest(10L)
        );

        assertEquals(20L, result.paymentId());
        assertEquals("MOCK", result.provider());
        assertEquals(PaymentStatus.PENDING, result.status());
        assertEquals(
                "http://localhost:4200/payment/mock/20",
                result.checkoutUrl()
        );
    }

    @Test
    void createPaymentReturnsExistingPendingPayment() {
        Payment existingPayment = new Payment();
        existingPayment.setId(20L);
        existingPayment.setOrder(pendingOrder);
        existingPayment.setAmount(new BigDecimal("99.80"));
        existingPayment.setStatus(PaymentStatus.PENDING);
        existingPayment.setProvider("MOCK");
        existingPayment.setCheckoutUrl(
                "http://localhost:4200/payment/mock/20"
        );

        when(orderRepository.findByIdAndUserId(10L, 7L))
                .thenReturn(Optional.of(pendingOrder));
        when(paymentRepository.findByOrderId(10L))
                .thenReturn(Optional.of(existingPayment));

        PaymentResponse result = paymentService.createPayment(
                7L,
                new PaymentRequest(10L)
        );

        assertEquals(20L, result.paymentId());
        verify(paymentProvider, never()).createPayment(any(), any(), any());
    }

    @Test
    void createPaymentRejectsNonPendingOrder() {
        pendingOrder.setStatus(OrderStatus.CANCELLED);
        when(orderRepository.findByIdAndUserId(10L, 7L))
                .thenReturn(Optional.of(pendingOrder));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.createPayment(
                        7L,
                        new PaymentRequest(10L)
                )
        );

        assertEquals(
                "Only pending orders can be paid",
                exception.getMessage()
        );
    }
}
