package io.redick01.ratelimiter.zk.example.controller;

import io.redick01.ratelimiter.annotation.RateLimiter;
import io.redick01.ratelimiter.example.dto.Request;
import io.redick01.ratelimiter.example.dto.Response;
import io.redick01.ratelimiter.example.retelimit.RateLimiterResponse1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Zookeeper example controller.
 *
 * @author Redick01
 */
@RestController
@Slf4j
public class ZookeeperTestController {

    /**
     * api1.
     * @param request {@link Request}
     * @return response
     */
    @PostMapping("/zk-rate/test1")
    @RateLimiter(key = "zk-rate-test1", clazz = RateLimiterResponse1.class)
    public Response<String> test1(@RequestBody Request request) {
        log.info("请求参数是:{}", request.toString());
        return new Response<>("0000", "成功", "success");
    }

    /**
     * api2.
     * @param request {@link Request}
     * @return Response
     */
    @PostMapping("/zk-rate/test2")
    @RateLimiter(key = "'/zk-rate/test2:' + #args[0].userId", clazz = RateLimiterResponse1.class)
    public Response<String> test2(@RequestBody Request request) {
        log.info("请求参数是:{}", request.toString());
        return new Response<>("0000", "成功", "success");
    }

}
