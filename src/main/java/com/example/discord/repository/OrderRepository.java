package com.example.discord.repository;

import com.example.discord.billingEntity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
}
