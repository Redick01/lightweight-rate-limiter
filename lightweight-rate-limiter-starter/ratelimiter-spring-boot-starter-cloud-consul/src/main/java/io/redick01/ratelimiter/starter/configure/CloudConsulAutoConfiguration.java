package io.redick01.ratelimiter.starter.configure;

import io.redick01.ratelimiter.starter.refresh.CloudConsulRefresher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.consul.config.ConsulConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Redick01
 */
@Configuration
@ConditionalOnClass(ConsulConfigProperties.class)
public class CloudConsulAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean()
    public CloudConsulRefresher cloudConsulRefresher() {
        return new CloudConsulRefresher();
    }
}
