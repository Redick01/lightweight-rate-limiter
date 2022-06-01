package io.redick01.ratelimiter.algorithm;

import io.redick01.ratelimiter.common.enums.RateLimitEnum;
import io.redick01.spi.Join;

/**
 * @author Redick01
 */
@Join
public class LeakyBucketRateLimiterAlgorithm extends AbstractRateLimiterAlgorithm {

    public LeakyBucketRateLimiterAlgorithm() {
        super(RateLimitEnum.LEAKY_BUCKET.getScriptName());
    }

    @Override
    protected String getKeyName() {
        return RateLimitEnum.LEAKY_BUCKET.getScriptName();
    }
}
