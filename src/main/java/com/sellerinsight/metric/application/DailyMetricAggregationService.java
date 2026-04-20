package com.sellerinsight.metric.application;

import com.sellerinsight.common.error.BusinessException;
import com.sellerinsight.common.error.ErrorCode;
import com.sellerinsight.metric.api.dto.DailyMetricResponse;
import com.sellerinsight.metric.domain.DailyMetric;
import com.sellerinsight.metric.domain.DailyMetricRepository;
import com.sellerinsight.order.domain.CustomerOrder;
import com.sellerinsight.order.domain.CustomerOrderRepository;
import com.sellerinsight.order.domain.OrderItemRepository;
import com.sellerinsight.product.domain.Product;
import com.sellerinsight.product.domain.ProductRepository;
import com.sellerinsight.seller.domain.Seller;
import com.sellerinsight.seller.domain.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyMetricAggregationService {

    private static final ZoneId ASIA_SEOUL = ZoneId.of("Asia/Seoul");

    private final SellerRepository sellerRepository;
    private final DailyMetricRepository dailyMetricRepository;
    private final ProductRepository productRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public DailyMetricResponse aggregate(Long sellerId, LocalDate metricDate) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        OffsetDateTime dayStart = metricDate.atStartOfDay(ASIA_SEOUL).toOffsetDateTime();
        OffsetDateTime nextDayStart = metricDate.plusDays(1).atStartOfDay(ASIA_SEOUL).toOffsetDateTime();

        List<CustomerOrder> orders = customerOrderRepository
                .findAllBySellerIdAndOrderedAtGreaterThanEqualAndOrderedAtLessThan(
                        sellerId,
                        dayStart,
                        nextDayStart
                );

        int orderCount = orders.size();
        BigDecimal salesAmount = orders.stream()
                .map(CustomerOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averageOrderAmount = orderCount == 0
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : salesAmount.divide(BigDecimal.valueOf(orderCount), 2, RoundingMode.HALF_UP);

        int soldOutProductCount = (int) productRepository.countBySellerIdAndStockQuantityLessThanEqual(
                sellerId,
                0
        );

        OffsetDateTime staleWindowStart = metricDate.minusDays(13)
                .atStartOfDay(ASIA_SEOUL)
                .toOffsetDateTime();

        Set<Long> recentlySoldProductIds = orderItemRepository.findDistinctProductIdsBySellerIdAndOrderedAtBetween(
                sellerId,
                staleWindowStart,
                nextDayStart
        );

        int staleProductCount = (int) productRepository.findAllBySellerId(sellerId).stream()
                .map(Product::getId)
                .filter(productId -> !recentlySoldProductIds.contains(productId))
                .count();

        DailyMetric dailyMetric = dailyMetricRepository.findBySellerIdAndMetricDate(sellerId, metricDate)
                .map(existingMetric -> {
                    existingMetric.update(
                            orderCount,
                            salesAmount,
                            averageOrderAmount,
                            soldOutProductCount,
                            staleProductCount
                    );
                    return existingMetric;
                })
                .orElseGet(() -> DailyMetric.create(
                        seller,
                        metricDate,
                        orderCount,
                        salesAmount,
                        averageOrderAmount,
                        soldOutProductCount,
                        staleProductCount
                ));

        return DailyMetricResponse.from(dailyMetricRepository.saveAndFlush(dailyMetric));
    }

    public DailyMetricResponse get(Long sellerId, LocalDate metricDate) {
        DailyMetric dailyMetric = dailyMetricRepository.findBySellerIdAndMetricDate(sellerId, metricDate)
                .orElseThrow(() -> new BusinessException(ErrorCode.DAILY_METRIC_NOT_FOUND));

        return DailyMetricResponse.from(dailyMetric);
    }
}
