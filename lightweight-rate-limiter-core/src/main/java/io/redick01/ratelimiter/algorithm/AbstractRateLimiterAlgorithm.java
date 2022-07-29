package io.redick01.ratelimiter.algorithm;

import io.redick01.ratelimiter.common.constant.Constant;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.Arrays;
import java.util.List;

/**
 * @author Redick01
 */
public abstract class AbstractRateLimiterAlgorithm implements RateLimiterAlgorithm<List<Long>> {

    private final String scriptName;

    private final RedisScript<List<Long>> script;

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected AbstractRateLimiterAlgorithm(final String scriptName) {
        DefaultRedisScript redisScript = new DefaultRedisScript<>();
        String scriptPath = Constant.SCRIPT_PATH + scriptName;
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(scriptPath)));
        redisScript.setResultType(List.class);
        this.script = redisScript;
        this.scriptName = scriptName;
    }

    /**
     * Gets key name.
     *
     * @return the key name
     */
    protected abstract String getKeyName();

    @Override
    public String getScriptName() {
        return scriptName;
    }

    @Override
    public RedisScript<List<Long>> getScript() {
        return script;
    }

    @Override
    public List<String> getKeys(final String id) {
        String prefix = getKeyName() + ".{" + id;
        String tokenKey = prefix + "}.tokens";
        String timestampKey = prefix + "}.timestamp";
        return Arrays.asList(tokenKey, timestampKey);
    }
}
