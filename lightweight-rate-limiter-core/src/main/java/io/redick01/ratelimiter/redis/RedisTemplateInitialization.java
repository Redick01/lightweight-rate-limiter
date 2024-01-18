package io.redick01.ratelimiter.redis;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import io.redick01.ratelimiter.common.config.RtProperties;
import io.redick01.ratelimiter.common.enums.Singleton;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Redick01
 */
public class RedisTemplateInitialization {

    public RedisTemplateInitialization(RtProperties rtProperties) {
        if (Objects.nonNull(rtProperties) && Objects.nonNull(rtProperties.getRedisConfig())) {
            //init redis
            RtProperties.RedisConfig redisConfig = rtProperties.getRedisConfig();
            //spring data redisTemplate
            if (Objects.isNull(Singleton.INST.get(RedisTemplate.class))) {
                LettuceConnectionFactory lettuceConnectionFactory = createLettuceConnectionFactory(redisConfig);
                RedisTemplate<String, Object> template = new RedisTemplate<>();
                RedisSerializer<String> serializer = new StringRedisSerializer();
                template.setConnectionFactory(lettuceConnectionFactory);
                template.setKeySerializer(serializer);
                template.setValueSerializer(serializer);
                template.setHashKeySerializer(serializer);
                template.setHashValueSerializer(serializer);
                template.afterPropertiesSet();
                Singleton.INST.single(RedisTemplate.class, template);
            }
        }
    }

    /**
     * create lettuce connection factory.
     * @param redisConfig {@link io.redick01.ratelimiter.common.config.RtProperties.RedisConfig}
     * @return LettuceConnectionFactory
     */
    private LettuceConnectionFactory createLettuceConnectionFactory(final RtProperties.RedisConfig redisConfig) {
        String mode = redisConfig.getMode();
        LettuceClientConfiguration lettuceClientConfiguration = getLettuceClientConfiguration(redisConfig);
        if ("sentinel".equals(mode)) {
            return new LettuceConnectionFactory(redisSentinelConfiguration(redisConfig));
        } else if ("cluster".equals(mode)) {
            return new LettuceConnectionFactory(redisClusterConfiguration(redisConfig));
        }
        return new LettuceConnectionFactory(redisStandaloneConfiguration(redisConfig));
    }

    private LettuceClientConfiguration getLettuceClientConfiguration(final RtProperties.RedisConfig redisConfig) {
        return LettucePoolingClientConfiguration.builder().poolConfig(getPoolConfig(redisConfig)).build();
    }


    /**
     * get pool.
     * @param redisConfig {@link io.redick01.ratelimiter.common.config.RtProperties.RedisConfig}
     * @return GenericObjectPoolConfig
     */
    private GenericObjectPoolConfig<?> getPoolConfig(final RtProperties.RedisConfig redisConfig) {
        GenericObjectPoolConfig<?> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(redisConfig.getMaxActive());
        config.setMaxIdle(redisConfig.getMaxIdle());
        config.setMinIdle(redisConfig.getMinIdle());
        if (redisConfig.getMaxWait() != null) {
            config.setMaxWaitMillis(redisConfig.getMaxWait().toMillis());
        }
        return config;
    }

    /**
     * redisSentinelConfiguration.
     * @param redisConfig {@link io.redick01.ratelimiter.common.config.RtProperties.RedisConfig}
     * @return redisSentinelConfiguration
     */
    private RedisSentinelConfiguration redisSentinelConfiguration(final RtProperties.RedisConfig redisConfig) {
        RedisSentinelConfiguration config = new RedisSentinelConfiguration();
        config.master(redisConfig.getMaster());
        config.setSentinels(createRedisNode(redisConfig.getUrl()));
        if (redisConfig.getPassword() != null) {
            config.setPassword(RedisPassword.of(redisConfig.getPassword()));
        }
        config.setDatabase(redisConfig.getDatabase());
        return config;
    }

    /**
     * redisClusterConfiguration.
     * @param redisConfig {@link io.redick01.ratelimiter.common.config.RtProperties.RedisConfig}
     * @return redisClusterConfiguration
     */
    private RedisClusterConfiguration redisClusterConfiguration(final RtProperties.RedisConfig redisConfig) {
        RedisClusterConfiguration config = new RedisClusterConfiguration();
        config.setClusterNodes(createRedisNode(redisConfig.getUrl()));
        if (redisConfig.getPassword() != null) {
            config.setPassword(RedisPassword.of(redisConfig.getPassword()));
        }
        return config;
    }

    /**
     * redisS tandalone.
     * @param redisConfig {@link io.redick01.ratelimiter.common.config.RtProperties.RedisConfig}
     * @return RedisStandaloneConfiguration
     */
    protected final RedisStandaloneConfiguration redisStandaloneConfiguration(
        final RtProperties.RedisConfig redisConfig) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        String[] parts = StringUtils.split(redisConfig.getUrl(), ":");
        assert parts != null;
        config.setHostName(parts[0]);
        config.setPort(Integer.parseInt(parts[1]));
        if (redisConfig.getPassword() != null) {
            config.setPassword(RedisPassword.of(redisConfig.getPassword()));
        }
        config.setDatabase(redisConfig.getDatabase());
        return config;
    }

    /**
     * createRedisNode.
     * @param url url
     * @return List<RedisNode>
     */
    private List<RedisNode> createRedisNode(final String url) {
        List<RedisNode> redisNodes = new ArrayList<>();
        List<String> nodes = Lists.newArrayList(Splitter.on(";").split(url));
        for (String node : nodes) {
            String[] parts = StringUtils.split(node, ":");
            Assert.state(Objects.requireNonNull(parts).length == 2, "Must be defined as 'host:port'");
            redisNodes.add(new RedisNode(parts[0], Integer.parseInt(parts[1])));
        }
        return redisNodes;
    }
}
