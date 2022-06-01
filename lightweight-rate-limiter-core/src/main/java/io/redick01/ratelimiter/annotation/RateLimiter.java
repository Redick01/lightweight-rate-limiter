package io.redick01.ratelimiter.annotation;

import java.lang.annotation.*;

/**
 * @author Redick01
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface RateLimiter {

    /**
     * rate limit key
     */
    String key();

    /**
     * rate limit callback
     */
    Class<?> clazz();
}
