package com.example.discord.toss;

public record TossConfirmRequest(String paymentKey, String orderId, Long amount) {}