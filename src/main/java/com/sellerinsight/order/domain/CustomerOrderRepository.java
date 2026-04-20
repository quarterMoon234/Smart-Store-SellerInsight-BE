package com.sellerinsight.order.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

    Optional<CustomerOrder> findBySellerIdAndExternalOrderNo(Long sellerId, String externalOrderNo);

    List<CustomerOrder> findAllBySellerIdAndOrderedAtGreaterThanEqualAndOrderedAtLessThan(
            Long sellerId,
            OffsetDateTime start,
            OffsetDateTime end
    );
}
