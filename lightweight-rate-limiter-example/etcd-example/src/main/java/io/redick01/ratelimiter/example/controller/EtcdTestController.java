package io.redick01.ratelimiter.example.controller;

import io.redick01.ratelimiter.annotation.RateLimiter;
import io.redick01.ratelimiter.example.dto.Request;
import io.redick01.ratelimiter.example.dto.Response;
import io.redick01.ratelimiter.example.retelimit.RateLimiterResponse1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Redick01
 */
@RestController
@Slf4j
public class EtcdTestController {

    /**
     * api1.
     * @param request {@link Request}
     * @return response
     */
    @PostMapping("/etcd-rate/test1")
    @RateLimiter(key = "etcd-rate-test1", clazz = RateLimiterResponse1.class)
    public Response<String> test1(@RequestBody Request request) {
        log.info("请求参数是:{}", request.toString());
        return new Response<>("0000", "成功", "success");
    }

    /**
     * api2.
     * @param request {@link Request}
     * @return Response
     */
    @PostMapping("/etcd-rate/test2")
    @RateLimiter(key = "'/etcd-rate/test2:' + #args[0].userId", clazz = RateLimiterResponse1.class)
    public Response<String> test2(@RequestBody Request request) {
        log.info("请求参数是:{}", request.toString());
        return new Response<>("0000", "成功", "success");
    }

}
