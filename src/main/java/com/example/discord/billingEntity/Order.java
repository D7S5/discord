package com.example.discord.billingEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    private String orderId;

    @Column(nullable = false)
    private Long serverId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanCode planCode;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    private OffsetDateTime createdAt;
    private OffsetDateTime paidAt;

    public static Order create(Long serverId, PlanCode planCode, long amount) {
        Order o = new Order();
        o.orderId = java.util.UUID.randomUUID().toString();
        o.serverId = serverId;
        o.planCode = planCode;
        o.amount = amount;
        o.status = OrderStatus.PENDING;
        o.createdAt = OffsetDateTime.now();
        return o;
    }

    public void markPaid(OffsetDateTime paidAt) {
        this.status = OrderStatus.PAID;
        this.paidAt = paidAt;
    }

    public boolean isPending() { return status == OrderStatus.PENDING; }
}
