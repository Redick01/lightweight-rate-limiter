package io.redick01.ratelimiter.common.enums;

/**
 * @author Redick01
 */
public enum RateLimitEnum {

    SLIDING_WINDOW("sliding_window_request_rate_limiter", "sliding_window_request_rate_limiter.lua"),

    LEAKY_BUCKET("request_leaky_rate_limiter", "request_leaky_rate_limiter.lua"),

    CONCURRENT("concurrent_request_rate_limiter", "concurrent_request_rate_limiter.lua"),

    TOKEN_BUCKET("request_rate_limiter", "request_rate_limiter.lua");

    private final String keyName;

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
