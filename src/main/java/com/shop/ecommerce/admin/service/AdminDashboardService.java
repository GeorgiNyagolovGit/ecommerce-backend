package com.shop.ecommerce.admin.service;

import com.shop.ecommerce.admin.dto.DashboardStatsDto;
import com.shop.ecommerce.auth.repository.UserRepository;
import com.shop.ecommerce.order.entity.OrderStatus;
import com.shop.ecommerce.order.repository.OrderRepository;
import com.shop.ecommerce.payment.entity.PaymentStatus;
import com.shop.ecommerce.payment.repository.PaymentRepository;
import com.shop.ecommerce.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public AdminDashboardService(
            UserRepository userRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository,
            PaymentRepository paymentRepository
    ) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    public DashboardStatsDto getStats() {
        return new DashboardStatsDto(
                userRepository.count(),
                productRepository.count(),
                orderRepository.count(),
                orderRepository.countByStatus(OrderStatus.PENDING),
                paymentRepository.countByStatus(PaymentStatus.COMPLETED),
                paymentRepository.sumCompletedAmount()
        );
    }
}
