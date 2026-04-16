package com.sellerinsight.commerce.auth;

import com.sellerinsight.common.error.BusinessException;
import com.sellerinsight.common.error.ErrorCode;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class NaverCommerceSignatureGenerator {

    public String generate(String clientId, String clientSecret, long timestamp) {

        try {
            String password = clientId + "_" + timestamp;
            String hashedPassword = BCrypt.hashpw(password, clientSecret);

            return Base64.getEncoder().encodeToString(
                    hashedPassword.getBytes(StandardCharsets.UTF_8)
            );
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(ErrorCode.NAVER_COMMERCE_SIGNATURE_FAILED);
        }
    }
}
