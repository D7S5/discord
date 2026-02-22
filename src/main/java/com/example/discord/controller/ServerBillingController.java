package com.example.discord.controller;

import com.example.discord.billingEntity.CreateOrderRequest;
import com.example.discord.billingEntity.CreateOrderResponse;
import com.example.discord.billingEntity.ServerPlanResponse;
import com.example.discord.security.UserPrincipal;
import com.example.discord.service.BillingService;
import com.example.discord.service.ServerSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/servers/{serverId}/billing")
public class ServerBillingController {

    private final BillingService billingService;

    private final ServerSubscriptionService subscriptionService;

    @PostMapping("/orders")
    public ResponseEntity<CreateOrderResponse> createOrder(
            @PathVariable Long serverId,
            @RequestBody CreateOrderRequest req,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(billingService.createOrder(serverId, user.getId(), req));
    }

    @GetMapping("/plan")
    public ServerPlanResponse getPlan(@PathVariable Long serverId) {
        return subscriptionService.getServerPlan(serverId);
    }
}