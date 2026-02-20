package com.example.discord.toss;

import com.example.discord.billingEntity.PlanCode;
import com.example.discord.billingEntity.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "server_subscriptions",
        uniqueConstraints = @UniqueConstraint(name="uk_server_subscription_server", columnNames="serverId")
)
public class ServerSubscription {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, length = 36)
    private Long serverId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanCode planCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;

    @Column(nullable = false)
    private OffsetDateTime currentPeriodStart;

    @Column(nullable = false)
    private OffsetDateTime currentPeriodEnd;

    @Column(nullable = false)
    private boolean cancelAtPeriodEnd;

    public static ServerSubscription createNew(Long serverId, PlanCode plan, OffsetDateTime now) {
        ServerSubscription s = new ServerSubscription();
        s.serverId = serverId;
        s.planCode = plan;
        s.status = SubscriptionStatus.ACTIVE;
        s.currentPeriodStart = now;
        s.currentPeriodEnd = now.plusMonths(1);
        s.cancelAtPeriodEnd = false;
        return s;
    }

    public boolean isActive(OffsetDateTime now) {
        return status == SubscriptionStatus.ACTIVE && now.isBefore(currentPeriodEnd);
    }

    public void upgradeNow(PlanCode newPlan) {
        this.planCode = newPlan;
        this.status = SubscriptionStatus.ACTIVE;
        this.cancelAtPeriodEnd = false;
    }

    public void extendOneMonth() {
        // 현재 기간이 남아있으면 end만 연장, 만료됐으면 새 기간 시작
        OffsetDateTime now = OffsetDateTime.now();
        if (now.isBefore(this.currentPeriodEnd)) {
            this.currentPeriodEnd = this.currentPeriodEnd.plusMonths(1);
        } else {
            this.currentPeriodStart = now;
            this.currentPeriodEnd = now.plusMonths(1);
            this.status = SubscriptionStatus.ACTIVE;
            this.cancelAtPeriodEnd = false;
        }
    }

    public void cancelAtPeriodEnd() {
        this.cancelAtPeriodEnd = true;
    }

    public void expireIfNeeded(OffsetDateTime now) {
        if (now.isAfter(currentPeriodEnd) && cancelAtPeriodEnd) {
            this.status = SubscriptionStatus.CANCELED;
            this.planCode = PlanCode.FREE; // 만료시 free
        }
    }
}
