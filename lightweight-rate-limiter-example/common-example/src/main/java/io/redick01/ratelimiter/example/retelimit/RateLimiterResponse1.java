package io.redick01.ratelimiter.example.retelimit;

import io.redick01.ratelimiter.callback.RateLimitCallback;
import io.redick01.ratelimiter.example.dto.Response;

/**
 * @author Redick01
 */
public class RateLimiterResponse1 implements RateLimitCallback<Response<String>> {

    @Override
    public Response<String> rateLimitReturn(Object[] args, String rateLimitKey) {
        return new Response<>("998", "限流", "rate limit");
    }
}
