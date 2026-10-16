package com.shop.ecommerce.order.service;

import com.shop.ecommerce.auth.entity.User;
import com.shop.ecommerce.auth.repository.UserRepository;
import com.shop.ecommerce.common.exception.NotFoundException;
import com.shop.ecommerce.order.dto.OrderDto;
import com.shop.ecommerce.order.entity.Order;
import com.shop.ecommerce.order.entity.OrderItem;
import com.shop.ecommerce.order.entity.OrderStatus;
import com.shop.ecommerce.order.repository.OrderRepository;
import com.shop.ecommerce.product.entity.Product;
import com.shop.ecommerce.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(
            OrderRepository orderRepository,
            UserRepository userRepository,
            ProductRepository productRepository
    ) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderDto createOrder(
            String email,
            String shippingAddress,
            List<OrderDto.OrderItemDto> requestedItems
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (shippingAddress == null || shippingAddress.isBlank()
                || shippingAddress.length() > 255) {
            throw new IllegalArgumentException("Invalid shipping address");
        }

        if (requestedItems == null || requestedItems.isEmpty()) {
            throw new IllegalArgumentException("Order must contain products");
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderDto.OrderItemDto requestedItem : requestedItems) {
            if (requestedItem.getProductId() == null
                    || requestedItem.getQuantity() == null
                    || requestedItem.getQuantity() < 1) {
                throw new IllegalArgumentException("Invalid order item");
            }

            Product product = productRepository
                    .findByIdAndActiveTrue(requestedItem.getProductId())
                    .orElseThrow(() ->
                            new NotFoundException("Product not found"));

            int quantity = requestedItem.getQuantity();

            if (product.getStock() < quantity) {
                throw new IllegalArgumentException(
                        "Insufficient stock for " + product.getName()
                );
            }

            product.setStock(product.getStock() - quantity);

            BigDecimal price = product.getPrice();
            BigDecimal subtotal = price.multiply(
                    BigDecimal.valueOf(quantity)
            );

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setPrice(price);

            items.add(item);
            totalAmount = totalAmount.add(subtotal);
        }

        order.setItems(items);
        order.setTotalAmount(totalAmount);

        return mapToDto(orderRepository.save(order));
    }

    @Transactional
    public List<OrderDto> getMyOrders(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return orderRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional
    public OrderDto getMyOrder(String email, Long orderId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Order order = orderRepository
                .findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new NotFoundException("Order not found"));

        return mapToDto(order);
    }

    @Transactional
    public OrderDto cancelOrder(String email, Long orderId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Order order = orderRepository
                .findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Only pending orders can be cancelled"
            );
        }

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED);

        return mapToDto(order);
    }

    private OrderDto mapToDto(Order order) {
        List<OrderDto.OrderItemDto> items = order.getItems()
                .stream()
                .map(item -> new OrderDto.OrderItemDto(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getPrice().multiply(
                                BigDecimal.valueOf(item.getQuantity())
                        )
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