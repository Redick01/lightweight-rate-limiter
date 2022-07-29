package io.redick01.ratelimiter.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Redick01
 */
@SpringBootApplication
public class EtcdApplication {

    /**
     * startup.
     * @param args parameter
     */
    public static void main(String[] args) {
        SpringApplication.run(EtcdApplication.class, args);
    }
}
