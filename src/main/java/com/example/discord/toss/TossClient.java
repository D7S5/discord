package com.example.discord.toss;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
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
        headers.setBasicAuth(secretKey, "", StandardCharsets.UTF_8);

        Map<String, Object> body = Map.of(
                "paymentKey", paymentKey,
                "orderId", orderId,
                "amount", amount
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            String raw = response.getBody();

            JsonNode json = objectMapper.readTree(raw);

            String approvedAtStr = json.hasNonNull("approvedAt") ? json.get("approvedAt").asText() : null;
            OffsetDateTime approvedAt = (approvedAtStr == null || approvedAtStr.isBlank())
                    ? null
                    : OffsetDateTime.parse(approvedAtStr);

            return new TossConfirmResult(
                    json.path("paymentKey").asText(null),
                    json.path("orderId").asText(null),
                    json.path("status").asText(null),
                    json.path("method").asText(null),
                    approvedAt,
                    raw
            );

        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("TOSS_CONFIRM_HTTP_" + e.getStatusCode().value() + ": " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("TOSS_CONFIRM_PARSE_ERROR", e);
        }
    }
}
