package io.redick01.ratelimiter.example.springboot3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.boot.data.redis.autoconfigure.DataRedisReactiveAutoConfiguration;
import org.springframework.boot.data.redis.autoconfigure.DataRedisRepositoriesAutoConfiguration;

/**
 * @author Redick01
 */
@SpringBootApplication(exclude = {DataRedisAutoConfiguration.class, DataRedisReactiveAutoConfiguration.class,
    DataRedisRepositoriesAutoConfiguration.class})
public class Application {

    /**
     * startup.
     * @param args parameter
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
