package io.redick01.ratelimiter.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;

/**
 * @author Redick01
 */
@SpringBootApplication(exclude = {RedisAutoConfiguration.class, RedisReactiveAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class})
public class Application {

    /**
     * startup.
     * @param args parameter
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
