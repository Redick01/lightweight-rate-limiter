package io.redick01.ratelimiter.monitor.collector;

import io.redick01.ratelimiter.monitor.RateLimiterMetrics;

/**
 * @author Redick01
 */
public interface Collector {

    void collect(final String appName, final RateLimiterMetrics rateLimiterMetrics);
}
