package com.example.discord.service;

import com.example.discord.billingEntity.Order;
import com.example.discord.billingEntity.Payment;
import com.example.discord.repository.OrderRepository;
import com.example.discord.repository.PaymentRepository;
import com.example.discord.toss.TossClient;
import com.example.discord.toss.TossConfirmRequest;
import com.example.discord.toss.TossConfirmResponse;
import com.example.discord.toss.TossConfirmResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class TossService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ServerSubscriptionService subscriptionService;
    private final TossClient tossClient;

    @Transactional
    public TossConfirmResponse confirm(TossConfirmRequest req) {
        Order order = orderRepository.findById(req.orderId())
                .orElseThrow(() -> new IllegalArgumentException("ORDER_NOT_FOUND"));

        if (!order.isPending()) throw new IllegalStateException("ORDER_NOT_PENDING");
        if (!order.getAmount().equals(req.amount())) throw new IllegalStateException("AMOUNT_MISMATCH");

        if (paymentRepository.existsByPaymentKey(req.paymentKey())) {
            var p = paymentRepository.findByPaymentKey(req.paymentKey()).orElseThrow();
            return new TossConfirmResponse(
                    p.getPaymentKey(), p.getOrderId(), p.getStatus(), p.getMethod(),
                    p.getApprovedAt() != null ? p.getApprovedAt().toString() : null
            );
        }

        TossConfirmResult toss = tossClient.confirm(req.paymentKey(), req.orderId(), req.amount());

        order.markPaid(OffsetDateTime.now());

        paymentRepository.save(
                Payment.of(
                        order.getOrderId(),
                        toss.paymentKey(),
                        toss.method(),
                        toss.status(),
                        toss.approvedAt(),
                        toss.rawJson()
                )
        );

        subscriptionService.activateOrExtend(order.getServerId(), order.getPlanCode());

        // ✅ 클라이언트 응답은 필요한 것만 내려주기
        return new TossConfirmResponse(
                toss.paymentKey(),
                toss.orderId(),
                toss.status(),
                toss.method(),
                toss.approvedAt() != null ? toss.approvedAt().toString() : null
        );
    }

}
