package io.redick01.ratelimiter.monitor.collector;

import io.redick01.ratelimiter.monitor.RateLimiterMetrics;

/**
 * @author Redick01
 */
public interface Collector {

    /**
     * collect metrics.
     *
     * @param appName application name
     * @param rateLimiterMetrics {@link RateLimiterMetrics}
     */
    void collect(String appName, RateLimiterMetrics rateLimiterMetrics);
}
