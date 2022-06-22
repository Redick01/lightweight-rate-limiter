package io.redick01.ratelimiter.registry;

import com.google.common.collect.Maps;
import io.redick01.ratelimiter.common.config.RateLimiterConfigProperties;
import io.redick01.ratelimiter.common.config.RtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Redick01
 */
@Slf4j
public class RateLimiterRegistry implements ApplicationRunner {

    public final RtProperties rtProperties;

    public RateLimiterRegistry(RtProperties rtProperties) {
        this.rtProperties = rtProperties;
    }

    public static final Map<String, RateLimiterConfigProperties> RATE_LIMITER_REGISTRY = Maps.newConcurrentMap();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        refresh(rtProperties);
    }

    public static void refresh(RtProperties rtProperties) {
        List<RateLimiterConfigProperties> rateLimiterConfigs = rtProperties.getRateLimiterConfigs();
        if (Objects.isNull(rateLimiterConfigs)) {
            log.debug("RateLimiterConfigProperties is null, no refresh rate limiter !");
            return;
        }
        rateLimiterConfigs.parallelStream().forEach(rateLimiterConfig -> {
            log.info("refresh rate limiter key : {}", rateLimiterConfig.toString());
            RATE_LIMITER_REGISTRY.put(rateLimiterConfig.getRateLimiterKey(), rateLimiterConfig);
        });
        log.info("refresh rate limiter registry completed!");
    }
}
