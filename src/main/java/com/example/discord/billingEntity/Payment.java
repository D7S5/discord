package com.example.discord.billingEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name="payments",
        uniqueConstraints = @UniqueConstraint(name="uk_payment_key", columnNames="paymentKey")
)
public class Payment {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable=false)
    private String orderId;

    @Column(nullable=false)
    private String paymentKey;

    private String method;

    @Column(nullable=false)
    private String status;

    private OffsetDateTime approvedAt;

    @Lob
    private String rawJson;

    public static Payment of(String orderId, String paymentKey, String method, String status,
                             OffsetDateTime approvedAt, String rawJson) {
        Payment p = new Payment();
        p.orderId = orderId;
        p.paymentKey = paymentKey;
        p.method = method;
        p.status = status;
        p.approvedAt = approvedAt;
        p.rawJson = rawJson;
        return p;
    }
}

