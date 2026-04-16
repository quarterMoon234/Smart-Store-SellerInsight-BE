package com.sellerinsight.seller.domain;

import com.sellerinsight.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "seller_credentials",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_seller_credentials_seller_id",
                        columnNames = "seller_id"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SellerCredential extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "seller_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_seller_credentials_seller_id")
    )
    private Seller seller;

    @Column(name = "client_id", nullable = false, length = 100)
    private String clientId;

    @Column(name = "encrypted_client_secret", nullable = false, columnDefinition = "text")
    private String encryptedClientSecret;

    @Column(name = "client_secret_hint", nullable = false, length = 4)
    private String clientSecretHint;

    private SellerCredential(
            Seller seller,
            String clientId,
            String encryptedClientSecret,
            String clientSecretHint
    ) {
        this.seller = seller;
        this.clientId = clientId;
        this.encryptedClientSecret = encryptedClientSecret;
        this.clientSecretHint = clientSecretHint;
    }

    public static SellerCredential create(
            Seller seller,
            String clientId,
            String encryptedClientSecret,
            String clientSecretHint
    ) {
        return new SellerCredential(seller, clientId, encryptedClientSecret, clientSecretHint);
    }

    public void update(
            String clientId,
            String encryptedClientSecret,
            String clientSecretHint
    ) {
        this.clientId = clientId;
        this.encryptedClientSecret = encryptedClientSecret;
        this.clientSecretHint = clientSecretHint;
    }
}

