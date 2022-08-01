package io.redick01.ratelimiter.starter.configure;

import io.redick01.ratelimiter.common.config.RtProperties;
import io.redick01.ratelimiter.common.util.PropertiesBinder;
import io.redick01.ratelimiter.starter.util.CuratorUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;

import java.util.Map;

/**
 * @author Redick01
 */
public class ZkConfigEnvironmentProcessor implements EnvironmentPostProcessor, Ordered {

    public static final String ZK_PROPERTY_SOURCE_NAME = "zkPropertySource";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        RtProperties rtProperties = new RtProperties();
        PropertiesBinder.bindRtProperties(environment, rtProperties);
        Map<Object, Object> properties = CuratorUtil.genPropertiesMap(rtProperties);
        if (!checkPropertyExist(environment)) {
            createZkPropertySource(environment, properties);
        }
    }

    /**
     * check environment property exist.
     * @param environment {@link ConfigurableEnvironment}
     * @return result
     */
    private boolean checkPropertyExist(ConfigurableEnvironment environment) {
        MutablePropertySources propertySources = environment.getPropertySources();
        return propertySources.stream().anyMatch(p -> ZK_PROPERTY_SOURCE_NAME.equals(p.getName()));
    }

    /**
     * create environment property.
     * @param environment {@link ConfigurableEnvironment}
     * @param properties config info
     */
    private void createZkPropertySource(ConfigurableEnvironment environment, Map<Object, Object> properties) {
        MutablePropertySources propertySources = environment.getPropertySources();
        OriginTrackedMapPropertySource zkSource =
            new OriginTrackedMapPropertySource(ZK_PROPERTY_SOURCE_NAME, properties);
        propertySources.addLast(zkSource);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
