package io.redick01.ratelimiter.example.retelimit;

import io.redick01.ratelimiter.callback.RateLimitCallback;

/**
 * @author Redick01
 */
public class RateLimiterResponse implements RateLimitCallback<String> {

    @Override
    public String rateLimitReturn(Object[] args, String rateLimitKey) {
        return "222";
    }
}
