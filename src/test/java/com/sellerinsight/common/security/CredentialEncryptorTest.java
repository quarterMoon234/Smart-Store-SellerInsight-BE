package com.sellerinsight.common.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class CredentialEncryptorTest {

    @Autowired
    private CredentialEncryptor credentialEncryptor;

    @Test
    void encryptAndDecrypt() {
        String plainText = "naver-secret-1234";

        String encrypted = credentialEncryptor.encrypt(plainText);
        String decrypted = credentialEncryptor.decrypt(encrypted);

        assertThat(encrypted).isNotEqualTo(plainText);
        assertThat(decrypted).isEqualTo(plainText);
    }
}
