package com.sellerinsight.pipeline.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.sellerinsight.insight.api.dto.InsightsByDateResponse;
import com.sellerinsight.insight.application.InsightGenerationService;
import com.sellerinsight.metric.application.DailyMetricAggregationService;
import com.sellerinsight.pipeline.api.dto.DailyPipelineRunResponse;
import com.sellerinsight.seller.domain.Seller;
import com.sellerinsight.seller.domain.SellerRepository;
import com.sellerinsight.seller.domain.SellerStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class DailyPipelineExecutionServiceTest {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private DailyMetricAggregationService dailyMetricAggregationService;

    @Mock
    private InsightGenerationService insightGenerationService;

    @Test
    void runProcessesConnectedSellersAndContinuesOnFailure() {
        Seller seller1 = mock(Seller.class);
        Seller seller2 = mock(Seller.class);

        when(seller1.getId()).thenReturn(1L);
        when(seller2.getId()).thenReturn(2L);

        LocalDate metricDate = LocalDate.of(2026, 4, 19);

        when(sellerRepository.findAllByStatus(SellerStatus.CONNECTED))
                .thenReturn(List.of(seller1, seller2));

        when(insightGenerationService.generate(eq(1L), eq(metricDate)))
                .thenReturn(new InsightsByDateResponse(1L, metricDate, 2, List.of()));

        when(dailyMetricAggregationService.aggregate(anyLong(), eq(metricDate)))
                .thenAnswer(invocation -> {
                    Long sellerId = invocation.getArgument(0);

                    if (sellerId.equals(2L)) {
                        throw new IllegalStateException("aggregation failed");
                    }

                    return null;
                });

        DailyPipelineExecutionService service = new DailyPipelineExecutionService(
                sellerRepository,
                dailyMetricAggregationService,
                insightGenerationService
        );

        DailyPipelineRunResponse result = service.run(metricDate);

        assertThat(result.metricDate()).isEqualTo(metricDate);
        assertThat(result.totalSellerCount()).isEqualTo(2);
        assertThat(result.processedSellerCount()).isEqualTo(1);
        assertThat(result.failedSellerCount()).isEqualTo(1);
        assertThat(result.generatedInsightCount()).isEqualTo(2);
        assertThat(result.failedSellerIds()).containsExactly(2L);

        verify(dailyMetricAggregationService).aggregate(1L, metricDate);
        verify(dailyMetricAggregationService).aggregate(2L, metricDate);
        verify(insightGenerationService).generate(1L, metricDate);
        verify(insightGenerationService, never()).generate(2L, metricDate);
    }
}
