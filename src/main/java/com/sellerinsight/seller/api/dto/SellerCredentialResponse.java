package com.sellerinsight.seller.api.dto;

import com.sellerinsight.seller.domain.SellerCredential;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;

public record SellerCredentialResponse(
        Long id,
        Long sellerId,
        String clientId,
        String clientSecretMasked,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static SellerCredentialResponse from(SellerCredential sellerCredential) {
        return new SellerCredentialResponse(
                sellerCredential.getId(),
                sellerCredential.getSeller().getId(),
                sellerCredential.getClientId(),
                mask(sellerCredential.getClientSecretHint()),
                sellerCredential.getCreatedAt(),
                sellerCredential.getUpdatedAt()
        );
    }

    private static String mask(String hint) {
        if (!StringUtils.hasText(hint)) {
            return "****";
        }
        return "****" + hint;
    }
}
