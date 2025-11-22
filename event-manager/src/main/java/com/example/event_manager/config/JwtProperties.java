package com.example.event_manager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    
    private String secret;
    private int accessExpirationMinutes;
    private int refreshExpirationDays;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getAccessExpirationMinutes() {
        return accessExpirationMinutes;
    }

    public void setAccessExpirationMinutes(int accessExpirationMinutes) {
        this.accessExpirationMinutes = accessExpirationMinutes;
    }

    public int getRefreshExpirationDays() {
        return refreshExpirationDays;
    }

    public void setRefreshExpirationDays(int refreshExpirationDays) {
        this.refreshExpirationDays = refreshExpirationDays;
    }
}