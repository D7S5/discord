package com.example.discord;

import com.example.discord.toss.TossProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(TossProperties.class)
public class DiscordApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscordApplication.class, args);
    }
}
