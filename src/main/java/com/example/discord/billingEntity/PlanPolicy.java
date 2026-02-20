package com.example.discord.billingEntity;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PlanPolicy {

    private final Map<PlanCode, PlanLimits> policy = Map.of(
            PlanCode.FREE, new PlanLimits(
                    0L,
                    25,
                    8L * 1024 * 1024,
                    10,
                    0
            ),
            PlanCode.PRO, new PlanLimits(
                    9900L,
                    200,
                    100L * 1024 * 1024,
                    50,
                    50
            ),
            PlanCode.TEAM, new PlanLimits(
                    29900L,
                    1000,
                    500L * 1024 * 1024,
                    200,
                    200
            )
    );

    public long priceMonthly(PlanCode code) {
        return get(code).priceMonthly();
    }

    public PlanLimits get(PlanCode code) {
        PlanLimits limits = policy.get(code);
        if (limits == null) {
            throw new IllegalArgumentException("INVALID_PLAN");
        }
        return limits;
    }
}
