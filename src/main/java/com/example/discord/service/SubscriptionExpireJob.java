package com.example.discord.service;

import com.example.discord.repository.ServerSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class SubscriptionExpireJob {

    private final ServerSubscriptionRepository subscriptionRepository;

    @Scheduled(cron = "0 0 3 * * *") // 매일 03:00
    @Transactional
    public void expireSubscriptions() {
        OffsetDateTime now = OffsetDateTime.now();
        var subs = subscriptionRepository.findAll();

        for (var sub : subs) {
            sub.expireIfNeeded(now);
        }
    }
}
