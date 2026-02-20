package com.example.discord.toss;

public interface PaymentSummary {
    String getPaymentKey();
    String getOrderId();
    String getStatus();
    String getMethod();
    java.time.OffsetDateTime getApprovedAt();
}
