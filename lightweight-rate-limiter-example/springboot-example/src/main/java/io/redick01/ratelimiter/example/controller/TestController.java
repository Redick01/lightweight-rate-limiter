package io.redick01.ratelimiter.example.controller;

import io.redick01.ratelimiter.annotation.RateLimiter;
import io.redick01.ratelimiter.example.dto.Request;
import io.redick01.ratelimiter.example.dto.Response;
import io.redick01.ratelimiter.example.retelimit.RateLimiterResponse;
import io.redick01.ratelimiter.example.retelimit.RateLimiterResponse1;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Redick01
 */
@RestController()
public class TestController {

    @GetMapping("/Rate/rateTest")
    @RateLimiter(key = "key1", clazz = RateLimiterResponse.class)
    public String rateTest() {
        return "111";
    }

    @PostMapping("/Rate/spelTest")
    @RateLimiter(key = "'/Rate/spelTest:' + #args[0].userId", clazz = RateLimiterResponse1.class)
    public Response<String> spelTest(@RequestBody Request request) {
        return new Response<>("0000", "成功", "success");
    }
}
