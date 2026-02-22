package com.example.discord.billingEntity;

public record PlanLimitsDto(
        int maxMembers,
        long maxUploadBytes,
        int maxVoiceParticipants,
        int maxEmojis
) {}
