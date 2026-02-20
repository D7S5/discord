package com.example.discord.service;

import com.example.discord.billingEntity.*;
import com.example.discord.entity.Role;
import com.example.discord.repository.OrderRepository;
import com.example.discord.repository.ServerMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BillingService {

    private final OrderRepository orderRepository;
    private final ServerMemberRepository serverMemberRepository; // 너 프로젝트에 이미 있을 확률 높음
    private final PlanPolicy planPolicy; // 가격/제한값을 서버가 결정

    public CreateOrderResponse createOrder(Long serverId, String userId, CreateOrderRequest req) {

        if (!serverMemberRepository.isOwner(
                serverId,
                userId)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN, "NOT_OWNER");
        }

        PlanCode planCode = PlanCode.valueOf(req.planCode());
        long amount = planPolicy.priceMonthly(planCode);

        Order order = Order.create(serverId, planCode, amount);
        orderRepository.save(order);

        return new CreateOrderResponse(order.getOrderId(), order.getAmount(), order.getPlanCode().name());
    }
}
