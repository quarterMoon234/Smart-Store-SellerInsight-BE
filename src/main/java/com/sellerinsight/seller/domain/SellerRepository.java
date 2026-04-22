package com.sellerinsight.seller.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    boolean existsByExternalSellerId(String externalSellerId);

    Optional<Seller> findByExternalSellerId(String externalSellerId);

    List<Seller> findAllByStatus(SellerStatus status);
}
