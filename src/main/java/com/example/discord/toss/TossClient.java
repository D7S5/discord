package com.example.discord.toss;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TossClient {

    @Value("${toss.secret-key}")
    private String secretKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TossConfirmResult confirm(String paymentKey, String orderId, long amount) {

        String url = "https://api.tosspayments.com/v1/payments/confirm";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(secretKey, ""); // 자동으로 base64 + ":" 처리됨 👍

        Map<String, Object> body = Map.of(
                "paymentKey", paymentKey,
                "orderId", orderId,
                "amount", amount
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(url, request, String.class);

        try {
            JsonNode json = objectMapper.readTree(response.getBody());

            return new TossConfirmResult(
                    json.get("paymentKey").asText(),
                    json.get("orderId").asText(),
                    json.get("status").asText(),
                    json.get("method").asText(),
                    OffsetDateTime.parse(json.get("approvedAt").asText()),
                    response.getBody()
            );

        } catch (Exception e) {
            throw new RuntimeException("TOSS_CONFIRM_PARSE_ERROR", e);
        }
    }
}