package io.redick01.ratelimiter.monitor;

import cn.hutool.core.thread.NamedThreadFactory;
import io.redick01.ratelimiter.common.config.RtProperties;
import io.redick01.ratelimiter.common.enums.Singleton;
import io.redick01.ratelimiter.monitor.collector.Collector;
import io.redick01.ratelimiter.monitor.collector.MetricsCollector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Redick01
 */
@Slf4j
public class Monitor implements ApplicationRunner, Ordered {

    private static final ScheduledExecutorService MONITOR_EXECUTOR = new ScheduledThreadPoolExecutor(
        1, new NamedThreadFactory("rate-limiter-monitor", true));

    private static final ScheduledExecutorService RECOVER_TOKEN_EXECUTOR = new ScheduledThreadPoolExecutor(
        1, new NamedThreadFactory("recover-token-executor", true));

    @Value("${spring.application.name}")
    private String appName;

    @Resource
    private RtProperties rtProperties;

    public Monitor() {
        Singleton.INST.single(Collector.class, new MetricsCollector());
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        MONITOR_EXECUTOR.scheduleWithFixedDelay(this::run,
                0, rtProperties.getMonitorInterval(), TimeUnit.SECONDS);

        RECOVER_TOKEN_EXECUTOR.scheduleWithFixedDelay(this::recover,
                3, 60, TimeUnit.SECONDS);
    }

    private void run() {
        if (rtProperties.getEnableMonitor()) {
            log.info("rate limiter metrics begin.");
            Map<String, RateLimiterMetrics> registry = MetricsRegistry.METRICS_MAP;
            registry.forEach((k, v) -> {
                Singleton.INST.get(Collector.class).collect(appName, v);
            });
        }
    }

    private void recover() {
        if (rtProperties.getEnableMonitor()) {
            log.info("recover the rate limiter metrics tokens left.");
            Map<String, RateLimiterMetrics> registry = MetricsRegistry.METRICS_MAP;
            registry.forEach((k, v) -> {
                RedisTemplate redisTemplate = Singleton.INST.get(RedisTemplate.class);
                Object res = redisTemplate.opsForValue().get(v.getRealKey());
                if (null == res) {
                    MetricsRegistry.recover(k, v.getCapacity().longValue());
                }
            });
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }
}
