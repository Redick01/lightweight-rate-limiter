package io.redick01.ratelimiter.redis;

import io.redick01.ratelimiter.common.config.RtProperties;
import io.redick01.ratelimiter.common.enums.Singleton;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.Arrays;
import java.util.List;

/**
 * @author Redick01
 */
public class RedisTemplateInitializationTest {

    private RtProperties rtProperties;

    private RedisTemplateInitialization redisTemplateInitialization;

    @BeforeEach
    public void startup() {
        rtProperties = new RtProperties();
        RtProperties.RedisConfig redisConfig = new RtProperties.RedisConfig();
        redisConfig.setDatabase(7);
        redisConfig.setUrl("127.0.0.1");
        redisConfig.setPassword("Qrbrqj88");
        rtProperties.setRedisConfig(redisConfig);
        redisTemplateInitialization = new RedisTemplateInitialization(rtProperties);
    }

    @Test
    public void init() {
        RedisTemplate redisTemplate = Singleton.INST.get(RedisTemplate.class);
        DefaultRedisScript redisScript = new DefaultRedisScript<>();
        String path = "/META-INF/scripts/request_rate_limiter.lua";
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(path)));
        redisScript.setResultType(List.class);
        double replenishRate = 10d;
        double burstCapacity = 10000d;
        double requestCount = 1000d;
        String tokenKey = "key.{" + "hhh" + "}.tokens";
        String timestampKey = "key.{" + "hhh" + "}.timestamp";
        List<Long> result = (List<Long>) Singleton.INST.get(RedisTemplate.class).execute(redisScript,
                Arrays.asList(tokenKey, timestampKey),
                replenishRate, burstCapacity, requestCount, 10000L);
        System.out.println(result);
    }
}
