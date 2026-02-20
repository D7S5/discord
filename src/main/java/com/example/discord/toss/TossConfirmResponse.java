package com.example.discord.toss;

public record TossConfirmResponse(String paymentKey, String orderId, String status, String method, String approvedAt) {}
