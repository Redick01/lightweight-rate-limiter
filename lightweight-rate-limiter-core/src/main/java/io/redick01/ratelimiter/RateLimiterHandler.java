package io.redick01.ratelimiter;

import io.redick01.ratelimiter.algorithm.RateLimiterAlgorithm;
import io.redick01.ratelimiter.common.config.RateLimiterConfigProperties;
import io.redick01.ratelimiter.common.enums.Singleton;
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


    public boolean isAllowed(final RateLimiterConfigProperties rateLimiterConfig) {
        RateLimiterAlgorithm<?> rateLimiterAlgorithm = ExtensionLoader
                .getExtensionLoader(RateLimiterAlgorithm.class)
                .getJoin(rateLimiterConfig.getAlgorithmName());
        RedisScript<?> redisScript = rateLimiterAlgorithm.getScript();
        List<String> keys = rateLimiterAlgorithm.getKeys(rateLimiterConfig.getRateLimiterKey());
        try {
            List<Long> result = (List<Long>) Singleton.INST.get(RedisTemplate.class).execute(redisScript,
                    keys,
                    doubleToString(rateLimiterConfig.getRate()),
                    doubleToString(rateLimiterConfig.getCapacity()),
                    doubleToString(Instant.now().getEpochSecond()),
                    doubleToString(1.0));
            assert result != null;
            Long tokensLeft = result.get(1);
            log.info("remain token count：{}", rateLimiterConfig.getCapacity() - tokensLeft);
            return result.get(0) == 1L;
        } finally {
            rateLimiterAlgorithm.callback(redisScript, keys);
        }
    }

    private String doubleToString(final double param) {
        return String.valueOf(param);
    }
}
