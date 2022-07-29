package io.redick01.ratelimiter.configure;

import io.redick01.ratelimiter.RateLimiterHandler;
import io.redick01.ratelimiter.banner.RateLimiterBanner;
import io.redick01.ratelimiter.monitor.Monitor;
import io.redick01.ratelimiter.registry.RateLimiterRegistry;
import io.redick01.ratelimiter.aop.RateLimiterInterceptor;
import io.redick01.ratelimiter.common.config.RtProperties;
import io.redick01.ratelimiter.redis.RedisTemplateInitialization;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author Redick01
 */
@Configuration
@EnableConfigurationProperties(RtProperties.class)
@EnableAspectJAutoProxy
public class BaseAutoConfiguration {

    /**
     * RedisTemplate init bean.
     * @param rtProperties {@link RtProperties}
     * @return RedisTemplateInitialization
     */
    @Bean
    @ConditionalOnMissingBean
    public RedisTemplateInitialization redisTemplateInitialization(RtProperties rtProperties) {
        return new RedisTemplateInitialization(rtProperties);
    }

    /**
     * rate limiter registry bean.
     * @param rtProperties {@link RtProperties}
     * @return RateLimiterRegistry
     */
    @Bean
    @ConditionalOnMissingBean
    public RateLimiterRegistry rateLimiterRegistry(RtProperties rtProperties) {
        return new RateLimiterRegistry(rtProperties);
    }

    /**
     * {@link RateLimiterHandler} bean.
     * @return RateLimiterHandler bean
     */
    @Bean
    @ConditionalOnMissingBean
    public RateLimiterHandler rateLimiterHandler() {
        return new RateLimiterHandler();
    }

    /**
     * {@link RateLimiterInterceptor} bean.
     * @return RateLimiterInterceptor
     */
    @Bean
    @ConditionalOnMissingBean
    public RateLimiterInterceptor rateLimiterInterceptor() {
        return new RateLimiterInterceptor();
    }

    /**
     * {@link RateLimiterBanner} bean.
     * @return RateLimiterBanner
     */
    @Bean
    public RateLimiterBanner rateLimiterBanner() {
        return new RateLimiterBanner();
    }

    /**
     * {@link Monitor} bean.
     * @return Monitor
     */
    @Bean
    @ConditionalOnMissingBean
    public Monitor monitor() {
        return new Monitor();
    }
}
