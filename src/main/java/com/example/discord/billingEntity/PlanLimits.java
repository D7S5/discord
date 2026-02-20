package com.example.discord.billingEntity;

public record PlanLimits(
        long priceMonthly,
        int maxMembers,
        long maxUploadBytes,
        int maxVoiceParticipants,
        int maxEmojis
) {}