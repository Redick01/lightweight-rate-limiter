package io.redick01.ratelimiter.starter.configure;

import io.redick01.ratelimiter.starter.refresh.CloudZookeeperRefresher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.zookeeper.config.ZookeeperConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Redick01
 */
@Configuration
@ConditionalOnClass(ZookeeperConfigProperties.class)
public class CloudZookeeperAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean()
    public CloudZookeeperRefresher cloudZookeeperRefresher() {
        return new CloudZookeeperRefresher();
    }
}
