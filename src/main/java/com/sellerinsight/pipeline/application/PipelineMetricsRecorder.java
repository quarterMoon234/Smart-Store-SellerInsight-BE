package com.sellerinsight.pipeline.application;

import com.sellerinsight.pipeline.domain.PipelineRun;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class PipelineMetricsRecorder {

    private final MeterRegistry meterRegistry;

    public void recordDailyPipelineRun(PipelineRun pipelineRun) {
        String pipelineType = pipelineRun.getPipelineType().name();
        String triggerType = pipelineRun.getTriggerType().name();
        String status = pipelineRun.getStatus().name();

        Counter.builder("daily.pipeline.runs")
                .description("Total daily pipeline runs")
                .tag("pipeline_type", pipelineType)
                .tag("trigger_type", triggerType)
                .tag("status", status)
                .register(meterRegistry)
                .increment();

        Timer.builder("daily.pipeline.duration")
                .description("Daily pipeline execution duration")
                .tag("pipeline_type", pipelineType)
                .tag("trigger_type", triggerType)
                .tag("status", status)
                .register(meterRegistry)
                .record(Duration.ofMillis(Math.max(pipelineRun.getDurationMs(), 0L)));

        Counter.builder("daily.pipeline.processed.sellers")
                .description("Total processed sellers in daily pipeline")
                .tag("pipeline_type", pipelineType)
                .tag("trigger_type", triggerType)
                .tag("status", status)
                .register(meterRegistry)
                .increment(pipelineRun.getProcessedSellerCount());

        Counter.builder("daily.pipeline.failed.sellers")
                .description("Total failed sellers in daily pipeline")
                .tag("pipeline_type", pipelineType)
                .tag("trigger_type", triggerType)
                .tag("status", status)
                .register(meterRegistry)
                .increment(pipelineRun.getFailedSellerCount());

        Counter.builder("daily.pipeline.generated.insights")
                .description("Total generated insights in daily pipeline")
                .tag("pipeline_type", pipelineType)
                .tag("trigger_type", triggerType)
                .tag("status", status)
                .register(meterRegistry)
                .increment(pipelineRun.getGeneratedInsightCount());
    }
}
