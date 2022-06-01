package io.redick01.ratelimiter;

import com.google.common.collect.Lists;
import io.redick01.ratelimiter.common.config.RateLimiterConfigProperties;
import io.redick01.ratelimiter.common.config.RtProperties;
import io.redick01.ratelimiter.redis.RedisTemplateInitialization;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author Redick01
 */
public class RateLimiterHandlerTest {

    private RtProperties rtProperties;

    private RateLimiterHandler rateLimiterHandler;

    private RedisTemplateInitialization redisTemplateInitialization;

    @BeforeEach
    public void startup() {
        rtProperties = new RtProperties();
        RtProperties.RedisConfig redisConfig = new RtProperties.RedisConfig();
        redisConfig.setDatabase(7);
        redisConfig.setUrl("127.0.0.1");
        redisConfig.setPassword("Qrbrqj88");
        rtProperties.setRedisConfig(redisConfig);
        RateLimiterConfigProperties rateLimiterConfig = new RateLimiterConfigProperties();
        List<RateLimiterConfigProperties> limiterConfigs = Lists.newArrayList();
        rateLimiterConfig.setRateLimiterKey("redisTemplateInitialization");
        rateLimiterConfig.setRate(1d);
        rateLimiterConfig.setAlgorithmName("token_bucket_rate_limiter");
        rateLimiterConfig.setCapacity(1000d);
        limiterConfigs.add(rateLimiterConfig);
        rtProperties.setRateLimiterConfigs(limiterConfigs);
        rateLimiterHandler = new RateLimiterHandler();
        redisTemplateInitialization = new RedisTemplateInitialization(rtProperties);
    }

    @Test
    public void isAllowed() {
        Assertions.assertTrue(rateLimiterHandler.isAllowed(rtProperties.getRateLimiterConfigs().get(0)));
    }
}
