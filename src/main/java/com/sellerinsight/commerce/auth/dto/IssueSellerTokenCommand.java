package com.sellerinsight.commerce.auth.dto;

public record IssueSellerTokenCommand (
        String clientId,
        String clientSecret,
        String accountId
) {
}
