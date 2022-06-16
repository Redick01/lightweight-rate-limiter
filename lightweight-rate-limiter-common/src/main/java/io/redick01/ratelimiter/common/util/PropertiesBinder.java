package io.redick01.ratelimiter.common.util;

import io.redick01.ratelimiter.common.config.RtProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;

import java.util.Map;

import static io.redick01.ratelimiter.common.constant.Constant.CONFIG_PREFIX;

/**
 * @author Redick01
 */
public class PropertiesBinder {

    private PropertiesBinder() {}

    public static void bindDtpProperties(Map<?, Object> properties, RtProperties rtProperties) {
        ConfigurationPropertySource sources = new MapConfigurationPropertySource(properties);
        Binder binder = new Binder(sources);
        ResolvableType type = ResolvableType.forClass(RtProperties.class);
        Bindable<?> target = Bindable.of(type).withExistingValue(rtProperties);
        binder.bind(CONFIG_PREFIX, target);
    }

    public static void bindDtpProperties(Environment environment, RtProperties rtProperties) {
        Binder binder = Binder.get(environment);
        ResolvableType type = ResolvableType.forClass(RtProperties.class);
        Bindable<?> target = Bindable.of(type).withExistingValue(rtProperties);
        binder.bind(CONFIG_PREFIX, target);
    }
}
