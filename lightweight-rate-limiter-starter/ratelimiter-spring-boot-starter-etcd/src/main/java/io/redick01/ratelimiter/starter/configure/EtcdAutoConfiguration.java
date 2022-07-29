package io.redick01.ratelimiter.starter.configure;

import io.etcd.jetcd.Client;
import io.redick01.ratelimiter.starter.refresh.EtcdRefresher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Redick01
 */
@Configuration
@ConditionalOnClass(value = Client.class)
public class EtcdAutoConfiguration {

    /**
     * {@link EtcdRefresher}.
     * @return EtcdRefresher
     */
    @Bean
    public EtcdRefresher etcdRefresher() {
        return new EtcdRefresher();
    }
}
