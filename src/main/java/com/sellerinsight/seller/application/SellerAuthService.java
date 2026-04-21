package com.sellerinsight.seller.application;

import com.sellerinsight.commerce.auth.NaverCommerceAuthClient;
import com.sellerinsight.commerce.auth.dto.CommerceAccessToken;
import com.sellerinsight.commerce.auth.dto.IssueSellerTokenCommand;
import com.sellerinsight.common.error.BusinessException;
import com.sellerinsight.common.error.ErrorCode;
import com.sellerinsight.common.security.CredentialEncryptor;
import com.sellerinsight.seller.api.dto.SellerAccessTokenResponse;
import com.sellerinsight.seller.domain.Seller;
import com.sellerinsight.seller.domain.SellerCredential;
import com.sellerinsight.seller.domain.SellerCredentialRepository;
import com.sellerinsight.seller.domain.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerAuthService {

    private final SellerRepository sellerRepository;
    private final SellerCredentialRepository sellerCredentialRepository;
    private final CredentialEncryptor credentialEncryptor;
    private final NaverCommerceAuthClient naverCommerceAuthClient;

    public SellerAccessTokenResponse issueSellerToken(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        SellerCredential sellerCredential = sellerCredentialRepository.findBySellerId(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SELLER_CREDENTIAL_NOT_FOUND));

        String decryptedClientSecret = credentialEncryptor.decrypt(
                sellerCredential.getEncryptedClientSecret()
        );

        CommerceAccessToken accessToken = naverCommerceAuthClient.issueSellerToken(
                new IssueSellerTokenCommand(
                        sellerCredential.getClientId(),
                        decryptedClientSecret,
                        seller.getExternalSellerId()
                )
        );

        return SellerAccessTokenResponse.of(
                sellerId,
                seller.getExternalSellerId(),
                accessToken
        );
    }
}
