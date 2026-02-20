package com.example.discord.toss;

import java.time.OffsetDateTime;

public record TossConfirmResult(
        String paymentKey,
        String orderId,
        String status,
        String method,
        OffsetDateTime approvedAt,
        String rawJson
) {

}
