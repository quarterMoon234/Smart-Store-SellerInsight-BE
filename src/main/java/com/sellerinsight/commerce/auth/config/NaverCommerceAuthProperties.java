package com.sellerinsight.commerce.auth.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "naver-commerce.auth")
public record NaverCommerceAuthProperties(
        @NotBlank String baseUrl,
        @NotBlank String tokenPath,
        @Positive int connectTimeoutMs,
        @Positive int readTimeoutSeconds
) {
}
