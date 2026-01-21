package com.azharkhalid.aitextsummarizer.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("SummarizeMetrics Tests")
class SummarizeMetricsTest {

    private MeterRegistry meterRegistry;
    private SummarizeMetrics metrics;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        metrics = new SummarizeMetrics(meterRegistry);
        metrics.init();
    }

    @Test
    @DisplayName("Should record incoming requests")
    void shouldRecordIncomingRequests() {
        metrics.recordRequest();
        metrics.recordRequest();
        metrics.recordRequest();

        assertThat(metrics.getRequestCount()).isEqualTo(3.0);
    }

    @Test
    @DisplayName("Should record successful requests")
    void shouldRecordSuccessfulRequests() {
        metrics.recordSuccess();
        metrics.recordSuccess();

        assertThat(metrics.getSuccessCount()).isEqualTo(2.0);
    }

    @Test
    @DisplayName("Should record failed requests")
    void shouldRecordFailedRequests() {
        metrics.recordFailure();

        assertThat(metrics.getFailureCount()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should record validation errors")
    void shouldRecordValidationErrors() {
        metrics.recordValidationError();
        metrics.recordValidationError();

        assertThat(meterRegistry.counter("summarize.errors.total", "error_type", "validation").count())
                .isEqualTo(2.0);
    }

    @Test
    @DisplayName("Should record timeout errors")
    void shouldRecordTimeoutErrors() {
        metrics.recordTimeout();

        assertThat(meterRegistry.counter("summarize.errors.total", "error_type", "timeout").count())
                .isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should record request duration")
    void shouldRecordRequestDuration() {
        metrics.recordRequestDuration(500);
        metrics.recordRequestDuration(1000);
        metrics.recordRequestDuration(750);

        var timer = meterRegistry.find("summarize.request.duration").timer();
        assertThat(timer).isNotNull();
        assertThat(timer.count()).isGreaterThan(0);
        assertThat(timer.totalTime(java.util.concurrent.TimeUnit.MILLISECONDS))
                .isGreaterThan(0);
    }

    @Test
    @DisplayName("Should update input length gauge")
    void shouldUpdateInputLengthGauge() {
        metrics.updateInputLength(5000);
        metrics.updateInputLength(10000);

        var gauge = meterRegistry.find("summarize.input.length").gauge();
        assertThat(gauge).isNotNull();
        assertThat(gauge.value()).isEqualTo(10000.0);
    }
}
