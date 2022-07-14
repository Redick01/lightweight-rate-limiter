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
                Tag.of(POOL_NAME_TAG, rateLimiterMetrics.getKey()),
                Tag.of(APP_NAME_TAG, appName),
                Tag.of(ALGORITHM_NAME_TAG, rateLimiterMetrics.getAlgorithmName()));
        Metrics.gauge(metricName(), tags, rateLimiterMetrics, this::tokensLeft);
    }

    private static String metricName() {
        return String.join(".", METRIC_NAME_PREFIX, "tokens.left");
    }

    private Long tokensLeft(RateLimiterMetrics rateLimiterMetrics) {
        //// TODO: 2022/7/14 1.准确计算剩余token 2.限流配置变更时发出事件更新 MetricsRegistry 3.恢复MetricsRegistry的剩余token
        if ("leaky_bucket_rate_limiter".equals(rateLimiterMetrics.getAlgorithmName())) {
            return 0L;
        }
        return 0L;
    }
}
