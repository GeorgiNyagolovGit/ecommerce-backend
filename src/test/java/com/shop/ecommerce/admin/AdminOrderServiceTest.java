package com.shop.ecommerce.admin;

import com.shop.ecommerce.admin.service.AdminOrderService;
import com.shop.ecommerce.auth.entity.User;
import com.shop.ecommerce.order.dto.OrderDto;
import com.shop.ecommerce.order.entity.Order;
import com.shop.ecommerce.order.entity.OrderItem;
import com.shop.ecommerce.order.entity.OrderStatus;
import com.shop.ecommerce.order.repository.OrderRepository;
import com.shop.ecommerce.product.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    private AdminOrderService orderService;
    private Order order;
    private Product product;

    @BeforeEach
    void setUp() {
        orderService = new AdminOrderService(orderRepository);

        User user = new User();
        user.setId(7L);
        user.setEmail("ivan@example.com");

        product = new Product();
        product.setId(1L);
        product.setName("Java Book");
        product.setStock(3);

        order = new Order();
        order.setId(10L);
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(new BigDecimal("99.80"));
        order.setShippingAddress("10 Bulgaria Blvd, Sofia");

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(2);
        item.setPrice(new BigDecimal("49.90"));
        order.setItems(List.of(item));
    }

    @Test
    void cancelPendingOrderRestoresStock() {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        OrderDto result = orderService.updateStatus(
                10L,
                OrderStatus.CANCELLED
        );

        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        assertEquals(5, product.getStock());
    }

    @Test
    void rejectsInvalidStatusTransition() {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.updateStatus(
                        10L,
                        OrderStatus.DELIVERED
                )
        );

        assertEquals(
                "Invalid order status transition from PENDING to DELIVERED",
                exception.getMessage()
        );
    }
}
