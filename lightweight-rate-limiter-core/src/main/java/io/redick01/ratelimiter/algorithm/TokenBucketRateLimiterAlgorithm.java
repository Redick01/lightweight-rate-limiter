package io.redick01.ratelimiter.algorithm;

import io.redick01.ratelimiter.common.enums.RateLimitEnum;
import io.redick01.spi.Join;

/**
 * @author Redick01
 */
@Join
public class TokenBucketRateLimiterAlgorithm extends AbstractRateLimiterAlgorithm {

    public TokenBucketRateLimiterAlgorithm() {
        super(RateLimitEnum.TOKEN_BUCKET.getScriptName());
    }

    @Override
    protected String getKeyName() {
        return RateLimitEnum.TOKEN_BUCKET.getKeyName();
    }
}
