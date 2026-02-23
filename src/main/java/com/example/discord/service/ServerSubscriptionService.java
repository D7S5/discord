package com.example.discord.service;

import com.example.discord.billingEntity.*;
import com.example.discord.repository.ServerSubscriptionRepository;
import com.example.discord.toss.ServerSubscription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ServerSubscriptionService {

    private final ServerSubscriptionRepository subscriptionRepository;
    private final PlanPolicy planPolicy;

    @Transactional
    public void activateOrExtend(Long serverId, PlanCode purchasedPlan) {
        OffsetDateTime now = OffsetDateTime.now();

        ServerSubscription sub = subscriptionRepository.findByServerId(serverId).orElse(null);

        if (sub == null) {
            subscriptionRepository.save(ServerSubscription.createNew(serverId, purchasedPlan, now));
            return;
        }

        int cmp = comparePlan(sub.getPlanCode(), purchasedPlan);

        if (cmp == 0) {
            sub.extendOneMonth();
        } else if (cmp < 0) {
            sub.upgradeNow(purchasedPlan);
        } else {
            throw new IllegalStateException("DOWNGRADE_NOT_ALLOWED");
        }
    }

    public PlanLimits getLimits(Long serverId) {
        // 없는 경우 FREE로 간주
        ServerSubscription sub = subscriptionRepository.findByServerId(serverId)
                .orElse(null);

        PlanCode plan = (sub == null) ? PlanCode.FREE : sub.getPlanCode();
        return planPolicy.get(plan);
    }

    private int comparePlan(PlanCode current, PlanCode target) {
        // FREE < PRO < TEAM
        return Integer.compare(rank(current), rank(target));
    }

    private int rank(PlanCode code) {
        return switch (code) {
            case FREE -> 0;
            case PRO -> 1;
            case TEAM -> 2;
        };
    }

    @Transactional(readOnly = true)
    public ServerPlanResponse getServerPlan(Long serverId) {

        var subOpt = subscriptionRepository.findByServerId(serverId);

        PlanCode planCode;
        SubscriptionStatus status;
        OffsetDateTime periodEnd;

        if (subOpt.isPresent()) {
            var sub = subOpt.get();
            planCode = sub.getPlanCode();
            status = sub.getStatus();
            periodEnd = sub.getCurrentPeriodEnd();
        } else {
            // 구독이 없으면 FREE 기본값
            planCode = PlanCode.FREE;
            status = SubscriptionStatus.ACTIVE;
            periodEnd = null;
        }

        var limits = planPolicy.get(planCode);

        return new ServerPlanResponse(
                planCode,
                status,
                periodEnd,
                new PlanLimitsDto(
                        limits.maxMembers(),
                        limits.maxUploadBytes(),
                        limits.maxVoiceParticipants(),
                        limits.maxEmojis()
                )
        );
    }
}