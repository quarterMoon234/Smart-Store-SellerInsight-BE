package com.sellerinsight.seller.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    boolean existsByExternalSellerId(String externalSellerId);

    List<Seller> findAllByStatus(SellerStatus status);
}
