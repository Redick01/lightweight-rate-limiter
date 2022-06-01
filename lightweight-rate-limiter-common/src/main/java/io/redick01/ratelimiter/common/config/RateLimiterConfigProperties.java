package io.redick01.ratelimiter.common.config;

import lombok.Data;

/**
 * @author Redick01
 */
@Data
public class RateLimiterConfigProperties {

    private String algorithmName = "concurrent_request_rate_limiter";

    private String rateLimiterKey;

    private Double capacity;

    private Double rate;
}
