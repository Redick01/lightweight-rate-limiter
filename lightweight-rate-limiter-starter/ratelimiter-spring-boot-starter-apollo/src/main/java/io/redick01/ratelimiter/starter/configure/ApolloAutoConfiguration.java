package io.redick01.ratelimiter.starter.configure;

import io.redick01.ratelimiter.starter.refresh.ApolloRefresher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Redick01
 */
@Configuration
public class ApolloAutoConfiguration {

    /**
     * {@link ApolloRefresher}.
     * @return ApolloRefresher
     */
    @Bean
    public ApolloRefresher apolloAutoConfiguration() {
        return new ApolloRefresher();
    }
}
