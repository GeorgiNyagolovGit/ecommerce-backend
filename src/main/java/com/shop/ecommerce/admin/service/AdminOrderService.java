package com.shop.ecommerce.admin.service;

import com.shop.ecommerce.common.exception.NotFoundException;
import com.shop.ecommerce.order.dto.OrderDto;
import com.shop.ecommerce.order.entity.Order;
import com.shop.ecommerce.order.entity.OrderStatus;
import com.shop.ecommerce.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AdminOrderService {

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS =
            Map.of(
                    OrderStatus.PENDING,
                    Set.of(OrderStatus.PAID, OrderStatus.CANCELLED),
                    OrderStatus.PAID,
                    Set.of(OrderStatus.SHIPPED),
                    OrderStatus.SHIPPED,
                    Set.of(OrderStatus.DELIVERED),
                    OrderStatus.DELIVERED,
                    Set.of(),
                    OrderStatus.CANCELLED,
                    Set.of()
            );

    private final OrderRepository orderRepository;

    public AdminOrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getOrders() {
        return orderRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderDto getOrder(Long id) {
        return toDto(findOrder(id));
    }

    @Transactional
    public OrderDto updateStatus(Long id, OrderStatus newStatus) {
        Order order = findOrder(id);
        OrderStatus currentStatus = order.getStatus();

        if (currentStatus == newStatus) {
            return toDto(order);
        }

        if (!ALLOWED_TRANSITIONS
                .getOrDefault(currentStatus, Set.of())
                .contains(newStatus)) {
            throw new IllegalArgumentException(
                    "Invalid order status transition from "
                            + currentStatus + " to " + newStatus
            );
        }

        if (newStatus == OrderStatus.CANCELLED) {
            order.getItems().forEach(item -> {
                int restoredStock = item.getProduct().getStock()
                        + item.getQuantity();
                item.getProduct().setStock(restoredStock);
            });
        }

        order.setStatus(newStatus);
        return toDto(order);
    }

    private Order findOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));
    }

    private OrderDto toDto(Order order) {
        List<OrderDto.OrderItemDto> items = order.getItems().stream()
                .map(item -> new OrderDto.OrderItemDto(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getPrice().multiply(BigDecimal.valueOf(
                                item.getQuantity()
                        ))
                ))
                .toList();

        return new OrderDto(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getEmail(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getShippingAddress(),
                order.getCreatedAt(),
                items
        );
    }
}
