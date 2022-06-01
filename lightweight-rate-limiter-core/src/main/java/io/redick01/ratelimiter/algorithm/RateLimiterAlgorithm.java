package io.redick01.ratelimiter.algorithm;

import io.redick01.spi.SPI;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;

/**
 * @author Redick01
 */
@SPI
public interface RateLimiterAlgorithm<T> {

    /**
     * Gets script name.
     *
     * @return the script name
     */
    String getScriptName();

    /**
     * Gets script.
     *
     * @return the script
     */
    RedisScript<T> getScript();

    /**
     * Gets keys.
     *
     * @param id the id
     * @return the keys
     */
    List<String> getKeys(String id);

    /**
     * Callback string.
     *
     * @param script     the script
     * @param keys       the keys
     */
    default void callback(final RedisScript<?> script, final List<String> keys) {
    }
}
