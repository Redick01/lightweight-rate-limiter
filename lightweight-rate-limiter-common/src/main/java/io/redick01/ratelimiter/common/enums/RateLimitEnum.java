package io.redick01.ratelimiter.common.enums;

/**
 * @author Redick01
 */
public enum RateLimitEnum {

    /**
     * 限流算法枚举
     */

    SLIDING_WINDOW("sliding_window_rate_limiter", "sliding_window_request_rate_limiter.lua"),

    LEAKY_BUCKET("leaky_bucket_rate_limiter", "request_leaky_rate_limiter.lua"),

    CONCURRENT("concurrent_request_rate_limiter", "concurrent_request_rate_limiter.lua"),

    TOKEN_BUCKET("token_bucket_rate_limiter", "request_rate_limiter.lua");

    /**
     * 算法名
     */
    private final String keyName;

    /**
     * 脚本名
     */
    private final String scriptName;

    RateLimitEnum(final String keyName, final String scriptName) {
        this.keyName = keyName;
        this.scriptName = scriptName;
    }

    /**
     * getKeyName.
     *
     * @return keyName
     */
    public String getKeyName() {
        return this.keyName;
    }

    /**
     * getScriptName.
     *
     * @return scriptName
     */
    public String getScriptName() {
        return this.scriptName;
    }
}
