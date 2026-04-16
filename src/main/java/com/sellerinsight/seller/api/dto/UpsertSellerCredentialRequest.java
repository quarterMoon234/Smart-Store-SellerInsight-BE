package com.sellerinsight.seller.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpsertSellerCredentialRequest(
        @NotBlank(message = "clientId는 필수입니다.")
        @Size(max = 100, message = "clientId는 100자 이하여야 합니다.")
        String clientId,

        @NotBlank(message = "clientSecret은 필수입니다.")
        @Size(max = 200, message = "clientSecret은 200자 이하여야 합니다.")
        String clientSecret
) {
}
