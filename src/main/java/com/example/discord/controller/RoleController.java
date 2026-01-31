package com.example.discord.controller;

import com.example.discord.entity.Role;
import com.example.discord.security.UserPrincipal;
import com.example.discord.service.RoleService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class RoleController {

    private final RoleService roleService;

    @PatchMapping("/servers/{serverId}/members/{userId}/role/toggle")
    public ResponseEntity<Void> changeRole(
            @PathVariable Long serverId,
            @PathVariable String userId,
            @AuthenticationPrincipal UserPrincipal auth
    ) {
        log.info("toggle role: server={}, target={}", serverId, userId);

        roleService.changeRole(
                serverId,
                userId,
                auth.getId()
        );
        return ResponseEntity.ok().build();
    }
}
