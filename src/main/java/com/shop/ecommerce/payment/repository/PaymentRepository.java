package com.shop.ecommerce.payment.repository;

import com.shop.ecommerce.payment.entity.Payment;
import com.shop.ecommerce.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);

    Optional<Payment> findByProviderPaymentId(String providerPaymentId);

    long countByStatus(PaymentStatus status);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p "
            + "WHERE p.status = com.shop.ecommerce.payment.entity.PaymentStatus.COMPLETED")
    java.math.BigDecimal sumCompletedAmount();
}
