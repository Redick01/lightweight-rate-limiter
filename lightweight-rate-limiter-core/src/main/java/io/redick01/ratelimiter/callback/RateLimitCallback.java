package io.redick01.ratelimiter.callback;

/**
 * @author Redick01
 */
public interface RateLimitCallback<T> {

    /**
     * 限流响应接口
     * @param args 参数
     * @param rateLimitKey 限流Key
     * @return 限流接口响应参数
     */
    T rateLimitReturn(Object[] args,String rateLimitKey);
}
