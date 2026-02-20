package com.example.discord.repository;

import com.example.discord.billingEntity.Payment;
import com.example.discord.toss.PaymentSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByPaymentKey(String paymentKey);

    Optional<Payment> findByPaymentKey(String paymentKey);

    Optional<Payment> findByOrderId(String orderId);
    @Query("""
        select p.paymentKey as paymentKey,
               p.orderId as orderId,
               p.status as status,
               p.method as method,
               p.approvedAt as approvedAt
        from Payment p
        where p.paymentKey = :paymentKey
    """)
    Optional<PaymentSummary> findSummaryByPaymentKey(@Param("paymentKey") String paymentKey);
}