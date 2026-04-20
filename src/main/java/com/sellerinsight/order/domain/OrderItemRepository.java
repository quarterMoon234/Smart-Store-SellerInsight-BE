package com.sellerinsight.order.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    Optional<OrderItem> findByCustomerOrderIdAndExternalOrderItemNo(
            Long customerOrderId,
            String externalOrderItemNo
    );

    @Query("""
            select distinct oi.product.id
            from OrderItem oi
            join oi.customerOrder o
            where o.seller.id = :sellerId
              and o.orderedAt >= :start
              and o.orderedAt < :end
            """)
    Set<Long> findDistinctProductIdsBySellerIdAndOrderedAtBetween(
            @Param("sellerId") Long sellerId,
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end
    );
}
