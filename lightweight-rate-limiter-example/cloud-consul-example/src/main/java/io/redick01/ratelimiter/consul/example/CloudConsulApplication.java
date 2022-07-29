package io.redick01.ratelimiter.consul.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Redick01
 */
@SpringBootApplication
public class CloudConsulApplication {

    /**
     * startup.
     * @param args parameter
     */
    public static void main(String[] args) {
        SpringApplication.run(CloudConsulApplication.class, args);
    }
}
