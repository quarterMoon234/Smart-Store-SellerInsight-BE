package com.sellerinsight.commerce.auth.dto;

import java.time.OffsetDateTime;

public record CommerceAccessToken(
        String accessToken,
        String tokenType,
        Long expiresIn,
        OffsetDateTime issuedAt,
        OffsetDateTime expiresAt
) {
}
