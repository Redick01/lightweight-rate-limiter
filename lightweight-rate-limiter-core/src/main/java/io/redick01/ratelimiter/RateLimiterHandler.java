package io.redick01.ratelimiter;

import io.redick01.ratelimiter.algorithm.RateLimiterAlgorithm;
import io.redick01.ratelimiter.common.config.RateLimiterConfigProperties;
import io.redick01.ratelimiter.common.enums.Singleton;
import io.redick01.ratelimiter.parser.script.ScriptParser;
import io.redick01.spi.ExtensionLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.time.Instant;
import java.util.List;

/**
 * @author Redick01
 */
@Slf4j
@SuppressWarnings("unchecked")
public class RateLimiterHandler {


    public boolean isAllowed(final RateLimiterConfigProperties rateLimiterConfig, final Object[] args) {
        RateLimiterAlgorithm<?> rateLimiterAlgorithm = ExtensionLoader
                .getExtensionLoader(RateLimiterAlgorithm.class)
                .getJoin(rateLimiterConfig.getAlgorithmName());
        RedisScript<?> redisScript = rateLimiterAlgorithm.getScript();
        ScriptParser parser = ExtensionLoader
                .getExtensionLoader(ScriptParser.class)
                .getJoin(rateLimiterConfig.getExpressionType());
        String realKey = parser.getExpressionValue(rateLimiterConfig.getRateLimiterKey(), args);
        List<String> keys = rateLimiterAlgorithm.getKeys(realKey);
        try {
            List<Long> result = (List<Long>) Singleton.INST.get(RedisTemplate.class).execute(redisScript,
                    keys,
                    doubleToString(rateLimiterConfig.getRate()),
                    doubleToString(rateLimiterConfig.getCapacity()),
                    doubleToString(Instant.now().getEpochSecond()),
                    doubleToString(1.0));
            assert result != null;
            Long tokensLeft = result.get(1);
            log.info("rate limiter core data: {}", tokensLeft);
            return result.get(0) == 1L;
        } finally {
            rateLimiterAlgorithm.callback(redisScript, keys);
        }
    }

    private String doubleToString(final double param) {
        return String.valueOf(param);
    }
}
