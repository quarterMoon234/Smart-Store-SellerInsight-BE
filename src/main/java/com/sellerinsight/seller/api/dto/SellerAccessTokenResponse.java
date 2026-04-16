package com.sellerinsight.seller.api.dto;

import com.sellerinsight.commerce.auth.dto.CommerceAccessToken;

import java.time.OffsetDateTime;

public record SellerAccessTokenResponse(
        Long sellerId,
        String accountId,
        String accessToken,
        String tokenType,
        Long expiresIn,
        OffsetDateTime issuedAt,
        OffsetDateTime expiresAt
) {
    public static SellerAccessTokenResponse of(
            Long sellerId,
            String accountId,
            CommerceAccessToken accessToken
    ) {
        return new SellerAccessTokenResponse(
                sellerId,
                accountId,
                accessToken.accessToken(),
                accessToken.tokenType(),
                accessToken.expiresIn(),
                accessToken.issuedAt(),
                accessToken.expiresAt()
        );
    }
}
