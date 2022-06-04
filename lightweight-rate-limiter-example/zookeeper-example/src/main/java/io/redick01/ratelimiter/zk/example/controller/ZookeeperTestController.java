package io.redick01.ratelimiter.zk.example.controller;

import io.redick01.ratelimiter.annotation.RateLimiter;
import io.redick01.ratelimiter.example.dto.Request;
import io.redick01.ratelimiter.example.dto.Response;
import io.redick01.ratelimiter.example.retelimit.RateLimiterResponse1;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Redick01
 */
@RestController
public class ZookeeperTestController {


    @PostMapping("/zk-rate/test1")
    @RateLimiter(key = "zk-rate-test1", clazz = RateLimiterResponse1.class)
    public Response<String> test1(@RequestBody Request request) {

        return new Response<>("0000", "成功", "success");
    }

}
