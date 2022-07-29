package io.redick01.ratelimiter.algorithm;

import io.redick01.ratelimiter.common.enums.RateLimitEnum;
import io.redick01.ratelimiter.common.enums.Singleton;
import io.redick01.spi.Join;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author Redick01
 */
@Join
public class ConcurrentRateLimiterAlgorithm extends AbstractRateLimiterAlgorithm {

    public ConcurrentRateLimiterAlgorithm() {
        super(RateLimitEnum.CONCURRENT.getScriptName());
    }

    @Override
    protected String getKeyName() {
        return RateLimitEnum.CONCURRENT.getScriptName();
    }

    @Override
    public List<String> getKeys(String id) {
        String tokenKey = getKeyName() + ".{" + id + "}.tokens";
        String requestKey = UUID.randomUUID().toString();
        return Arrays.asList(tokenKey, requestKey);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void callback(RedisScript<?> script, List<String> keys) {
        Singleton.INST.get(RedisTemplate.class).opsForZSet().remove(keys.get(0), keys.get(1));
    }
}
