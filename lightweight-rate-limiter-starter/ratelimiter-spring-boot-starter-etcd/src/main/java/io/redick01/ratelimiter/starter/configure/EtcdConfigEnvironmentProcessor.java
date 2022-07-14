package io.redick01.ratelimiter.starter.configure;

import io.redick01.ratelimiter.common.config.RtProperties;
import io.redick01.ratelimiter.common.util.PropertiesBinder;
import io.redick01.ratelimiter.starter.util.EtcdUtil;
import lombok.SneakyThrows;
import lombok.val;
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
public class EtcdConfigEnvironmentProcessor implements EnvironmentPostProcessor, Ordered {

    public static final String ETCD_PROPERTY_SOURCE_NAME = "etcdPropertySource";


    @SneakyThrows
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        RtProperties rtProperties = new RtProperties();
        PropertiesBinder.bindDtpProperties(environment, rtProperties);
        RtProperties.Etcd etcd = rtProperties.getEtcd();
        val properties = EtcdUtil.getConfigContent(etcd, rtProperties.getConfigType());
        if (!checkPropertyExist(environment)) {
            createPropertySource(environment, properties);
        }
    }

    private boolean checkPropertyExist(ConfigurableEnvironment environment) {
        MutablePropertySources propertySources = environment.getPropertySources();
        return propertySources.stream().anyMatch(p -> ETCD_PROPERTY_SOURCE_NAME.equals(p.getName()));
    }

    private void createPropertySource(ConfigurableEnvironment environment, Map<Object, Object> properties) {
        MutablePropertySources propertySources = environment.getPropertySources();
        OriginTrackedMapPropertySource zkSource = new OriginTrackedMapPropertySource(ETCD_PROPERTY_SOURCE_NAME, properties);
        propertySources.addLast(zkSource);
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
