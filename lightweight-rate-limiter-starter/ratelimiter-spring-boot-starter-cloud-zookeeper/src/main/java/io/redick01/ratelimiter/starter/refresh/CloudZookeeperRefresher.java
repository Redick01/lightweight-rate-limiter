package io.redick01.ratelimiter.starter.refresh;

import io.redick01.ratelimiter.common.config.RtProperties;
import io.redick01.ratelimiter.refresh.AbstractRefresher;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.lang.NonNull;

import javax.annotation.Resource;

/**
 * @author Redick01
 */
public class CloudZookeeperRefresher extends AbstractRefresher implements SmartApplicationListener {

    @Resource
    private RtProperties rtProperties;

    @Override
    public boolean supportsEventType(@NonNull Class<? extends ApplicationEvent> eventType) {
        return RefreshScopeRefreshedEvent.class.isAssignableFrom(eventType);
    }

    @Override
    public void onApplicationEvent(@NonNull ApplicationEvent event) {
        if (event instanceof RefreshScopeRefreshedEvent) {
            doRefresh(rtProperties);
        }
    }
}
