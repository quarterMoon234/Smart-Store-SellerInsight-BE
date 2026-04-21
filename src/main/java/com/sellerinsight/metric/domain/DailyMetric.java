package com.sellerinsight.metric.domain;

import com.sellerinsight.common.entity.BaseEntity;
import com.sellerinsight.seller.domain.Seller;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Entity
@Table(
        name = "daily_metrics",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_daily_metrics_seller_id_metric_date",
                        columnNames = {"seller_id", "metric_date"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyMetric extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "seller_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_daily_metrics_seller_id")
    )
    private Seller seller;

    @Column(name = "metric_date", nullable = false)
    private LocalDate metricDate;

    @Column(name = "order_count", nullable = false)
    private int orderCount;

    @Column(name = "sales_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal salesAmount;

    @Column(name = "average_order_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal averageOrderAmount;

    @Column(name = "sold_out_product_count", nullable = false)
    private int soldOutProductCount;

    @Column(name = "stale_product_count", nullable = false)
    private int staleProductCount;

    private DailyMetric(
            Seller seller,
            LocalDate metricDate,
            int orderCount,
            BigDecimal salesAmount,
            BigDecimal averageOrderAmount,
            int soldOutProductCount,
            int staleProductCount
    ) {
        this.seller = seller;
        this.metricDate = metricDate;
        this.orderCount = orderCount;
        this.salesAmount = salesAmount;
        this.averageOrderAmount = averageOrderAmount;
        this.soldOutProductCount = soldOutProductCount;
        this.staleProductCount = staleProductCount;
    }

    public static DailyMetric create(
            Seller seller,
            LocalDate metricDate,
            int orderCount,
            BigDecimal salesAmount,
            BigDecimal averageOrderAmount,
            int soldOutProductCount,
            int staleProductCount
    ) {
        return new DailyMetric(
                seller,
                metricDate,
                orderCount,
                salesAmount,
                averageOrderAmount,
                soldOutProductCount,
                staleProductCount
        );
    }

    public void update(
            int orderCount,
            BigDecimal salesAmount,
            BigDecimal averageOrderAmount,
            int soldOutProductCount,
            int staleProductCount
    ) {
        this.orderCount = orderCount;
        this.salesAmount = salesAmount;
        this.averageOrderAmount = averageOrderAmount;
        this.soldOutProductCount = soldOutProductCount;
        this.staleProductCount = staleProductCount;
    }
}
