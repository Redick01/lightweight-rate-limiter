package io.redick01.ratelimiter.monitor;

import lombok.Builder;
import lombok.Data;

/**
 * @author Redick01
 */
@Data
@Builder
public class RateLimiterMetrics {

    private String algorithmName;

    private String key;

    private Long tokensLeft;
}
