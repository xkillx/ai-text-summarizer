package com.azharkhalid.aitextsummarizer.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Custom metrics component for tracking summarization performance.
 * Uses Micrometer to expose metrics to Prometheus and other monitoring systems.
 */
@Slf4j
@Component
public class SummarizeMetrics {

    private final MeterRegistry meterRegistry;

    // Counters for tracking events
    private Counter requestCounter;
    private Counter successCounter;
    private Counter failureCounter;
    private Counter validationErrorCounter;
    private Counter timeoutCounter;

    // Timer for tracking request duration
    private Timer requestTimer;

    // Gauge for tracking current input length being processed
    private final AtomicLong currentInputLength = new AtomicLong(0);

    public SummarizeMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * Initialize all metrics.
     * Called automatically by Spring after dependency injection.
     */
    @PostConstruct
    public void init() {
        // Request counter - tracks all incoming requests
        this.requestCounter = Counter.builder("summarize.requests.total")
                .description("Total number of summarization requests")
                .tag("type", "incoming")
                .register(meterRegistry);

        // Success counter - tracks successful summarizations
        this.successCounter = Counter.builder("summarize.requests.total")
                .description("Number of successful summarizations")
                .tag("type", "success")
                .register(meterRegistry);

        // Failure counter - tracks failed summarizations
        this.failureCounter = Counter.builder("summarize.requests.total")
                .description("Number of failed summarizations")
                .tag("type", "failure")
                .register(meterRegistry);

        // Validation error counter - tracks validation failures
        this.validationErrorCounter = Counter.builder("summarize.errors.total")
                .description("Number of validation errors")
                .tag("error_type", "validation")
                .register(meterRegistry);

        // Timeout counter - tracks timeout errors
        this.timeoutCounter = Counter.builder("summarize.errors.total")
                .description("Number of timeout errors")
                .tag("error_type", "timeout")
                .register(meterRegistry);

        // Request timer - tracks processing time
        this.requestTimer = Timer.builder("summarize.request.duration")
                .description("Summarization request processing time")
                .tag("operation", "summarize")
                .register(meterRegistry);

        // Input length gauge - tracks current input length
        Gauge.builder("summarize.input.length", currentInputLength, AtomicLong::get)
                .description("Current input text length being processed")
                .tag("type", "input_size")
                .register(meterRegistry);

        log.info("SummarizeMetrics initialized successfully");
    }

    /**
     * Record a new incoming request.
     */
    public void recordRequest() {
        requestCounter.increment();
        log.debug("Request counter incremented: {}", requestCounter.count());
    }

    /**
     * Record a successful summarization.
     */
    public void recordSuccess() {
        successCounter.increment();
        log.debug("Success counter incremented: {}", successCounter.count());
    }

    /**
     * Record a failed summarization.
     */
    public void recordFailure() {
        failureCounter.increment();
        log.debug("Failure counter incremented: {}", failureCounter.count());
    }

    /**
     * Record a validation error.
     */
    public void recordValidationError() {
        validationErrorCounter.increment();
        log.debug("Validation error counter incremented: {}", validationErrorCounter.count());
    }

    /**
     * Record a timeout error.
     */
    public void recordTimeout() {
        timeoutCounter.increment();
        log.debug("Timeout counter incremented: {}", timeoutCounter.count());
    }

    /**
     * Record the processing time for a request.
     *
     * @param durationMs Processing time in milliseconds
     */
    public void recordRequestDuration(long durationMs) {
        requestTimer.record(java.time.Duration.ofMillis(durationMs));
        log.debug("Request duration recorded: {} ms", durationMs);
    }

    /**
     * Update the current input length gauge.
     *
     * @param length Input text length in characters
     */
    public void updateInputLength(long length) {
        currentInputLength.set(length);
        log.debug("Input length updated: {} characters", length);
    }

    /**
     * Get the current request count.
     */
    public double getRequestCount() {
        return requestCounter.count();
    }

    /**
     * Get the current success count.
     */
    public double getSuccessCount() {
        return successCounter.count();
    }

    /**
     * Get the current failure count.
     */
    public double getFailureCount() {
        return failureCounter.count();
    }
}
