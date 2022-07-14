package io.redick01.ratelimiter.monitor;

import io.redick01.ratelimiter.common.config.RateLimiterConfigProperties;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Redick01
 */
public class MetricsRegistry implements ApplicationRunner, Ordered {

    public static final Map<String, RateLimiterMetrics> METRICS_MAP = new ConcurrentHashMap<>(16);


    @Override
    public void run(ApplicationArguments args) throws Exception {

    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 3;
    }

    public static void refresh(final String key, final String realKey, final Long tokensLeft, final RateLimiterConfigProperties properties) {
        if (METRICS_MAP.containsKey(realKey)) {
            RateLimiterMetrics metrics = METRICS_MAP.get(key);
            metrics.setTokensLeft(tokensLeft);
        } else {
            RateLimiterMetrics metrics = RateLimiterMetrics.builder()
                    .key(realKey)
                    .algorithmName(properties.getAlgorithmName())
                    .tokensLeft(tokensLeft)
                    .build();
            METRICS_MAP.put(realKey, metrics);
        }
    }
}
