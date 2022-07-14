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

    @Bean
    @ConditionalOnMissingBean
    public RedisTemplateInitialization redisTemplateInitialization(RtProperties rtProperties) {
        return new RedisTemplateInitialization(rtProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public RateLimiterRegistry rateLimiterRegistry(RtProperties rtProperties) {
        return new RateLimiterRegistry(rtProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public RateLimiterHandler rateLimiterHandler() {
        return new RateLimiterHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public RateLimiterInterceptor rateLimiterInterceptor() {
        return new RateLimiterInterceptor();
    }

    @Bean
    public RateLimiterBanner rateLimiterBanner() {
        return new RateLimiterBanner();
    }

    @Bean
    @ConditionalOnMissingBean
    public Monitor monitor() {
        return new Monitor();
    }
}
