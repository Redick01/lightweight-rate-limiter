package io.redick01.ratelimiter.starter.configure;

import io.redick01.ratelimiter.starter.refresh.NacosRefresher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Redick01
 */
@Configuration
@ConditionalOnClass(value = com.alibaba.nacos.api.config.ConfigService.class)
public class NacosAutoConfiguration {

    /**
     * {@link NacosRefresher}.
     * @return NacosRefresher
     */
    @Bean
    public NacosRefresher nacosRefresher() {
        return new NacosRefresher();
    }
}
