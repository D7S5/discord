package com.example.discord.billingEntity;


public record CreateOrderResponse(String orderId, Long amount, String planCode) {}
