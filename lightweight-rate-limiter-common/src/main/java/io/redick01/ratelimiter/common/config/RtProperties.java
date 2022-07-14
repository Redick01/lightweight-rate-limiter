package io.redick01.ratelimiter.common.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.List;

import static io.redick01.ratelimiter.common.constant.Constant.CONFIG_PREFIX;

/**
 * @author Redick01
 */
@Slf4j
@Data
@ConfigurationProperties(prefix = CONFIG_PREFIX)
public class RtProperties {

    private String configType = "yml";

    private Zookeeper zookeeper;

    private Nacos nacos;

    private Apollo apollo;

    private Etcd etcd;

    private Boolean enableMonitor = false;

    private Integer monitorInterval = 5;

    private List<RateLimiterConfigProperties> rateLimiterConfigs;

    private RedisConfig redisConfig;


    @Data
    public static class Zookeeper {

        private String zkConnectStr;

        private String configVersion;

        private String rootNode;

        private String node;

        private String configKey;
    }

    @Data
    public static class Nacos {

        private String dataId;

        private String group;
    }

    @Data
    public static class Apollo {

        private String namespace;
    }

    @Data
    public static class Etcd {

        private String endpoints;

        private String user;

        private String password;

        private String charset = "UTF-8";

        private Boolean authEnable = false;

        private String authority = "ssl";

        private String key;
    }

    @Data
    public static class RedisConfig {

        private Integer database = 0;

        private String master;

        /**
         * If it is cluster or sentinel mode, separated with `;`.
         */
        private String url;

        /**
         * the password.
         */
        private String password;

        /**
         * Maximum number of "idle" connections in the pool. Use a negative value to
         * indicate an unlimited number of idle connections.
         */
        private int maxIdle = 8;

        /**
         * Target for the minimum number of idle connections to maintain in the pool. This
         * setting only has an effect if it is positive.
         */
        private int minIdle;

        /**
         * Maximum number of connections that can be allocated by the pool at a given
         * time. Use a negative value for no limit.
         */
        private int maxActive = 8;

        /**
         * Maximum amount of time a connection allocation should block before throwing an
         * exception when the pool is exhausted. Use a negative value to block
         * indefinitely.
         */
        private Duration maxWait = Duration.ofMillis(-1);
    }
}
