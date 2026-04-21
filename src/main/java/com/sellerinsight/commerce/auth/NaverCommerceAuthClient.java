package com.sellerinsight.commerce.auth;

import com.sellerinsight.commerce.auth.config.NaverCommerceAuthProperties;
import com.sellerinsight.commerce.auth.dto.CommerceAccessToken;
import com.sellerinsight.commerce.auth.dto.IssueSellerTokenCommand;
import com.sellerinsight.commerce.auth.dto.NaverCommerceTokenResponse;
import com.sellerinsight.common.error.BusinessException;
import com.sellerinsight.common.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.time.OffsetDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class NaverCommerceAuthClient {

    private static final ZoneId ASIA_SEOUL = ZoneId.of("Asia/Seoul");

    @Qualifier("naverCommerceAuthWebClient")
    private final WebClient webClient;
    private final NaverCommerceAuthProperties properties;
    private final NaverCommerceSignatureGenerator signatureGenerator;

    public CommerceAccessToken issueSellerToken(IssueSellerTokenCommand command) {
        long timestamp = System.currentTimeMillis();
        String signature = signatureGenerator.generate(
                command.clientId(),
                command.clientSecret(),
                timestamp
        );

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", command.clientId());
        formData.add("timestamp", String.valueOf(timestamp));
        formData.add("client_secret_sign", signature);
        formData.add("grant_type", "client_credentials");
        formData.add("type", "SELLER");
        formData.add("account_id", command.accountId());

        try {
            NaverCommerceTokenResponse response = webClient.post()
                    .uri(properties.tokenPath())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .defaultIfEmpty("")
                                    .map(body -> new BusinessException(ErrorCode.NAVER_COMMERCE_AUTH_FAILED))
                    )
                    .bodyToMono(NaverCommerceTokenResponse.class)
                    .block();

            if (response == null
                    || !StringUtils.hasText(response.accessToken())
                    || !StringUtils.hasText(response.tokenType())
                    || response.expiresIn() == null) {
                throw new BusinessException(ErrorCode.NAVER_COMMERCE_AUTH_INVALID_RESPONSE);
            }

            OffsetDateTime issuedAt = OffsetDateTime.now(ASIA_SEOUL);

            return new CommerceAccessToken(
                    response.accessToken(),
                    response.tokenType(),
                    response.expiresIn(),
                    issuedAt,
                    issuedAt.plusSeconds(response.expiresIn())
            );
        } catch (BusinessException exception) {
            throw exception;
        } catch (WebClientRequestException exception) {
            throw new BusinessException(ErrorCode.NAVER_COMMERCE_AUTH_FAILED);
        }
    }
}
