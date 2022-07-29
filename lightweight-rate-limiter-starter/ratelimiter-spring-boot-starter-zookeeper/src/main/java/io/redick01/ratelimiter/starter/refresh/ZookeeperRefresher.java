package io.redick01.ratelimiter.starter.refresh;

import io.redick01.ratelimiter.common.config.RtProperties;
import io.redick01.ratelimiter.refresh.AbstractRefresher;
import io.redick01.ratelimiter.starter.configure.ZkConfigEnvironmentProcessor;
import io.redick01.ratelimiter.starter.util.CuratorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.WatchedEvent;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

/**
 * @author Redick01
 */
@Slf4j
public class ZookeeperRefresher extends AbstractRefresher implements EnvironmentAware, InitializingBean {

    @Resource
    private RtProperties rtProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        final ConnectionStateListener connectionStateListener = (client, newState) -> {
            if (newState == ConnectionState.RECONNECTED) {
                loadAndRefresh();
            }
        };
        final CuratorListener curatorListener = (client, curatorEvent) -> {
            final WatchedEvent watchedEvent = curatorEvent.getWatchedEvent();
            if (null != watchedEvent) {
                switch (watchedEvent.getType()) {
                    case NodeChildrenChanged:
                    case NodeDataChanged:
                        loadAndRefresh();
                        break;
                    default:
                        break;
                }
            }
        };
        CuratorFramework curatorFramework = CuratorUtil.getCuratorFramework(rtProperties);
        String nodePath = CuratorUtil.nodePath(rtProperties);
        curatorFramework.getConnectionStateListenable().addListener(connectionStateListener);
        curatorFramework.getCuratorListenable().addListener(curatorListener);
        log.info("Add listener success, nodePath: {}", nodePath);
    }

    private void loadAndRefresh() {
        doRefresh(CuratorUtil.genPropertiesMap(rtProperties));
    }

    @Override
    public void setEnvironment(Environment environment) {
        ConfigurableEnvironment env = (ConfigurableEnvironment) environment;
        env.getPropertySources().remove(
            ZkConfigEnvironmentProcessor.ZK_PROPERTY_SOURCE_NAME);
    }
}
