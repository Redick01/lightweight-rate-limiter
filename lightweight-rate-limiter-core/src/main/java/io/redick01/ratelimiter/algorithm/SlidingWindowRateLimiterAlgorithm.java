package io.redick01.ratelimiter.algorithm;

import io.redick01.ratelimiter.common.enums.RateLimitEnum;
import io.redick01.spi.Join;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author Redick01
 */
@Join
public class SlidingWindowRateLimiterAlgorithm extends AbstractRateLimiterAlgorithm {

    public SlidingWindowRateLimiterAlgorithm() {
        super(RateLimitEnum.SLIDING_WINDOW.getScriptName());
    }

    @Override
    protected String getKeyName() {
        return RateLimitEnum.SLIDING_WINDOW.getKeyName();
    }

    @Override
    public List<String> getKeys(final String id) {
        String prefix = getKeyName() + ".{" + id;
        String tokenKey = prefix + "}.tokens";
        String timestampKey = UUID.randomUUID().toString();
        return Arrays.asList(tokenKey, timestampKey);
    }
}
