package io.redick01.ratelimiter.monitor.collector;

import com.google.common.collect.Lists;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.redick01.ratelimiter.monitor.RateLimiterMetrics;

/**
 * @author Redick01
 */
public class MetricsCollector implements Collector {

    public static final String METRIC_NAME_PREFIX = "rate.limiter";

    public static final String POOL_NAME_TAG = METRIC_NAME_PREFIX + ".key";

    public static final String APP_NAME_TAG = "app.name";

    public static final String ALGORITHM_NAME_TAG = "algorithm.name";

    @Override
    public void collect(String appName, RateLimiterMetrics rateLimiterMetrics) {
        Iterable<Tag> tags = Lists.newArrayList(
                Tag.of(POOL_NAME_TAG, rateLimiterMetrics.getRealKey()),
                Tag.of(APP_NAME_TAG, appName),
                Tag.of(ALGORITHM_NAME_TAG, rateLimiterMetrics.getAlgorithmName()));
        Metrics.gauge(metricName("capacity"), tags, rateLimiterMetrics, RateLimiterMetrics::getCapacity);
        Metrics.gauge(metricName("rate"), tags, rateLimiterMetrics, RateLimiterMetrics::getRate);
        Metrics.gauge(metricName("tokens.left"), tags, rateLimiterMetrics, this::tokensLeft);
        Metrics.gauge(metricName("reject.count"), tags, rateLimiterMetrics, this::getRejectCount);
    }

    private static String metricName(String name) {
        return String.join(".", METRIC_NAME_PREFIX, name);
    }

    private double tokensLeft(RateLimiterMetrics rateLimiterMetrics) {
        if ("leaky_bucket_rate_limiter".equals(rateLimiterMetrics.getAlgorithmName())) {
            return rateLimiterMetrics.getTokensLeft();
        }
        return rateLimiterMetrics.getCapacity() - rateLimiterMetrics.getTokensLeft();
    }

    private Integer getRejectCount(RateLimiterMetrics rateLimiterMetrics) {
        return rateLimiterMetrics.getRejectCount().getCount();
    }
}
