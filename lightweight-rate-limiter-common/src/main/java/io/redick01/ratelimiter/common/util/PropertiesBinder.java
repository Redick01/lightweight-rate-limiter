package io.redick01.ratelimiter.common.util;

import io.redick01.ratelimiter.common.config.RtProperties;
import io.redick01.ratelimiter.common.constant.Constant;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;

import java.util.Map;

/**
 * @author Redick01
 */
public final class PropertiesBinder {

    private PropertiesBinder() { }

    /**
     * environment properties bind to {@link RtProperties}.
     *
     * @param properties environment properties
     * @param rtProperties {@link RtProperties}
     */
    public static void bindRtProperties(Map<?, Object> properties, RtProperties rtProperties) {
        ConfigurationPropertySource sources = new MapConfigurationPropertySource(properties);
        Binder binder = new Binder(sources);
        ResolvableType type = ResolvableType.forClass(RtProperties.class);
        Bindable<?> target = Bindable.of(type).withExistingValue(rtProperties);
        binder.bind(Constant.CONFIG_PREFIX, target);
    }

    /**
     * environment properties bind to {@link RtProperties}.
     *
     * @param environment environment properties
     * @param rtProperties {@link RtProperties}
     */
    public static void bindRtProperties(Environment environment, RtProperties rtProperties) {
        Binder binder = Binder.get(environment);
        ResolvableType type = ResolvableType.forClass(RtProperties.class);
        Bindable<?> target = Bindable.of(type).withExistingValue(rtProperties);
        binder.bind(Constant.CONFIG_PREFIX, target);
    }
}
