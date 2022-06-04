package io.redick01.ratelimiter.refresh;

import cn.hutool.core.map.MapUtil;
import io.redick01.ratelimiter.common.config.RtProperties;
import io.redick01.ratelimiter.common.enums.ConfigFileTypeEnum;
import io.redick01.ratelimiter.common.util.PropertiesBinder;
import io.redick01.ratelimiter.registry.RateLimiterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * @author Redick01
 */
@Slf4j
public abstract class AbstractRefresher implements Refresher {

    @Resource
    private RtProperties rtProperties;

    @Override
    public void refresh(String content, ConfigFileTypeEnum fileType) {

    }

    protected void doRefresh(Map<Object, Object> properties) {
        if (MapUtil.isEmpty(properties)) {
            log.warn("DynamicTp refresh, empty properties.");
            return;
        }
        PropertiesBinder.bindDtpProperties(properties, rtProperties);
        RateLimiterRegistry.refresh(rtProperties);
    }
}
