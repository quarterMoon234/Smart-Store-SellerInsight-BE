package com.sellerinsight.insight.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sellerinsight.importjob.domain.ImportJobRepository;
import com.sellerinsight.insight.domain.InsightRepository;
import com.sellerinsight.insight.domain.RecommendationRepository;
import com.sellerinsight.metric.domain.DailyMetric;
import com.sellerinsight.metric.domain.DailyMetricRepository;
import com.sellerinsight.order.domain.CustomerOrderRepository;
import com.sellerinsight.order.domain.OrderItemRepository;
import com.sellerinsight.pipeline.domain.PipelineRunItemRepository;
import com.sellerinsight.pipeline.domain.PipelineRunRepository;
import com.sellerinsight.product.domain.ProductRepository;
import com.sellerinsight.seller.domain.Seller;
import com.sellerinsight.seller.domain.SellerCredentialRepository;
import com.sellerinsight.seller.domain.SellerRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class InsightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private InsightRepository insightRepository;

    @Autowired
    private DailyMetricRepository dailyMetricRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImportJobRepository importJobRepository;

    @Autowired
    private SellerCredentialRepository sellerCredentialRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private PipelineRunItemRepository pipelineRunItemRepository;

    @Autowired
    private PipelineRunRepository pipelineRunRepository;

    @BeforeEach
    void setUp() {
        pipelineRunItemRepository.deleteAll();
        pipelineRunRepository.deleteAll();
        recommendationRepository.deleteAll();
        insightRepository.deleteAll();
        dailyMetricRepository.deleteAll();
        orderItemRepository.deleteAll();
        customerOrderRepository.deleteAll();
        productRepository.deleteAll();
        importJobRepository.deleteAll();
        sellerCredentialRepository.deleteAll();
        sellerRepository.deleteAll();
    }

    @Test
    void generateInsights() throws Exception {
        Seller seller = sellerRepository.saveAndFlush(
                Seller.create("seller-001", "sellerinsight-store")
        );

        dailyMetricRepository.saveAndFlush(
                DailyMetric.create(
                        seller,
                        LocalDate.of(2026, 4, 15),
                        10,
                        new BigDecimal("100000"),
                        new BigDecimal("10000"),
                        0,
                        0
                )
        );

        dailyMetricRepository.saveAndFlush(
                DailyMetric.create(
                        seller,
                        LocalDate.of(2026, 4, 16),
                        5,
                        new BigDecimal("30000"),
                        new BigDecimal("6000"),
                        2,
                        1
                )
        );

        mockMvc.perform(
                        post("/api/v1/sellers/{sellerId}/insights/generate", seller.getId())
                                .param("date", "2026-04-16")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.metricDate").value("2026-04-16"))
                .andExpect(jsonPath("$.data.insightCount").value(4))
                .andExpect(jsonPath("$.data.insights[0].insightType").value("ORDER_DROP"))
                .andExpect(jsonPath("$.data.insights[1].insightType").value("SOLD_OUT_RISK"))
                .andExpect(jsonPath("$.data.insights[2].insightType").value("STALE_PRODUCT_RISK"))
                .andExpect(jsonPath("$.data.insights[3].insightType").value("AVERAGE_ORDER_AMOUNT_DROP"))
                .andExpect(jsonPath("$.data.insights[0].recommendations[0].priority").value(1))
                .andExpect(jsonPath("$.data.insights[3].recommendations[0].priority").value(1));

        Long insightId = insightRepository.findAll().get(0).getId();

        mockMvc.perform(
                        get("/api/v1/sellers/{sellerId}/insights", seller.getId())
                                .param("date", "2026-04-16")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.insightCount").value(4));

        mockMvc.perform(
                        get("/api/v1/sellers/{sellerId}/insights/{insightId}", seller.getId(), insightId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(insightId));
    }

    @Test
    void generateNoOrderInsight() throws Exception {
        Seller seller = sellerRepository.saveAndFlush(
                Seller.create("seller-002", "no-order-store")
        );

        dailyMetricRepository.saveAndFlush(
                DailyMetric.create(
                        seller,
                        LocalDate.of(2026, 4, 15),
                        4,
                        new BigDecimal("80000"),
                        new BigDecimal("20000"),
                        0,
                        0
                )
        );

        dailyMetricRepository.saveAndFlush(
                DailyMetric.create(
                        seller,
                        LocalDate.of(2026, 4, 16),
                        0,
                        BigDecimal.ZERO.setScale(2),
                        BigDecimal.ZERO.setScale(2),
                        0,
                        0
                )
        );

        mockMvc.perform(
                        post("/api/v1/sellers/{sellerId}/insights/generate", seller.getId())
                                .param("date", "2026-04-16")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.metricDate").value("2026-04-16"))
                .andExpect(jsonPath("$.data.insightCount").value(3))
                .andExpect(jsonPath("$.data.insights[0].insightType").value("ORDER_DROP"))
                .andExpect(jsonPath("$.data.insights[1].insightType").value("NO_ORDER"))
                .andExpect(jsonPath("$.data.insights[2].insightType").value("AVERAGE_ORDER_AMOUNT_DROP"));
    }
}
