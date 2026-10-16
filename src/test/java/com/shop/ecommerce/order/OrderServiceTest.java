package com.shop.ecommerce.order;

import com.shop.ecommerce.auth.entity.User;
import com.shop.ecommerce.auth.repository.UserRepository;
import com.shop.ecommerce.order.dto.OrderDto;
import com.shop.ecommerce.order.entity.Order;
import com.shop.ecommerce.order.entity.OrderStatus;
import com.shop.ecommerce.order.repository.OrderRepository;
import com.shop.ecommerce.order.service.OrderService;
import com.shop.ecommerce.product.entity.Product;
import com.shop.ecommerce.product.repository.ProductRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    private OrderService orderService;
    private User user;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(
                orderRepository,
                userRepository,
                productRepository
        );

        user = new User();
        user.setId(7L);
        user.setEmail("ivan@example.com");
    }

    @Test
    void createOrderCalculatesTotalAndReducesStock() {
        Product product = product(1L, "Java Book", "49.90", 5);
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(productRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(10L);
                    return order;
                });

        OrderDto result = orderService.createOrder(
                user.getEmail(),
                "10 Bulgaria Blvd, Sofia",
                List.of(requestedItem(1L, 2))
        );

        assertEquals(10L, result.getId());
        assertEquals(new BigDecimal("99.80"), result.getTotalAmount());
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals(3, product.getStock());
    }

    @Test
    void createOrderRejectsInsufficientStock() {
        Product product = product(1L, "Java Book", "49.90", 1);
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(productRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(product));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(
                        user.getEmail(),
                        "10 Bulgaria Blvd, Sofia",
                        List.of(requestedItem(1L, 2))
                )
        );

        assertEquals(
                "Insufficient stock for Java Book",
                exception.getMessage()
        );
        assertEquals(1, product.getStock());
    }

    @Test
    void cancelOrderRestoresStock() {
        Product product = product(1L, "Java Book", "49.90", 3);
        Order order = new Order();
        order.setId(10L);
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress("10 Bulgaria Blvd, Sofia");
        order.setTotalAmount(new BigDecimal("99.80"));

        com.shop.ecommerce.order.entity.OrderItem item =
                new com.shop.ecommerce.order.entity.OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(2);
        item.setPrice(product.getPrice());
        order.setItems(List.of(item));

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(orderRepository.findByIdAndUserId(10L, 7L))
                .thenReturn(Optional.of(order));

        OrderDto result = orderService.cancelOrder(user.getEmail(), 10L);

        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        assertEquals(5, product.getStock());
    }

    private Product product(
            Long id,
            String name,
            String price,
            int stock
    ) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(new BigDecimal(price));
        product.setStock(stock);
        product.setActive(true);
        return product;
    }

    private OrderDto.OrderItemDto requestedItem(
            Long productId,
            int quantity
    ) {
        return new OrderDto.OrderItemDto(
                productId,
                null,
                quantity,
                null,
                null
        );
    }
}
