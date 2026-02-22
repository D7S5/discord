package com.example.discord.billingEntity;

import java.time.OffsetDateTime;

public record ServerPlanResponse(
        PlanCode planCode,
        SubscriptionStatus status,
        OffsetDateTime currentPeriodEnd,
        PlanLimitsDto limits
) {}
