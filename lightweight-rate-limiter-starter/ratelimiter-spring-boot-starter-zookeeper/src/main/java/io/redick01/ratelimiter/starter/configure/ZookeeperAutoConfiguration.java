package io.redick01.ratelimiter.starter.configure;

import io.redick01.ratelimiter.starter.refresh.ZookeeperRefresher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Redick01
 */
@Configuration
@ConditionalOnClass(value = org.apache.curator.framework.CuratorFramework.class)
public class ZookeeperAutoConfiguration {

    @Bean
    public ZookeeperRefresher zookeeperRefresher() {
        return new ZookeeperRefresher();
    }
}
