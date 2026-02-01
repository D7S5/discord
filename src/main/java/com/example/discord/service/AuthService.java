package com.example.discord.service;

import com.example.discord.dto.LoginRequest;
import com.example.discord.dto.TokenResponse;
import com.example.discord.dto.register.RegisterRequest;
import com.example.discord.entity.User;
import com.example.discord.repository.UserRepository;
import com.example.discord.security.JwtProvider;
import com.example.discord.security.UserPrincipal;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    public TokenResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

//        User user = userRepository.findByEmail(principal.getEmail())
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String accessToken = jwtProvider.generateToken(principal.getId());

        TokenResponse res = new TokenResponse(accessToken);

        return res;
    }

    @Transactional
    public String register(RegisterRequest request) {
        try {
            User user = User.builder()
                    .email(request.getEmail())
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .build();

            userRepository.save(user);
            return user.getId();

        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("EMAIL_OR_USERNAME_ALREADY_EXISTS");
        }
    }
}
