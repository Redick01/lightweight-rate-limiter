package io.redick01.ratelimiter.starter.util;

import com.google.common.collect.Maps;
import io.redick01.ratelimiter.common.config.RtProperties;
import io.redick01.ratelimiter.common.enums.ConfigFileTypeEnum;
import io.redick01.ratelimiter.parser.config.ConfigParser;
import io.redick01.spi.ExtensionLoader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author Redick01
 */
@Slf4j
public final class CuratorUtil {

    private static final int BASE_SLEEP_TIME_MS = 1000;

    private static final int MAX_RETRY = 3;

    private static CuratorFramework curatorFramework;

    private static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);

    private CuratorUtil() { }

    /**
     * create {@link CuratorFramework}.
     * @param rtProperties {@link RtProperties}
     * @return CuratorFramework
     */
    public static CuratorFramework getCuratorFramework(RtProperties rtProperties) {
        if (curatorFramework == null) {
            RtProperties.Zookeeper zookeeper = rtProperties.getZookeeper();
            curatorFramework = CuratorFrameworkFactory.newClient(zookeeper.getZkConnectStr(),
                new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRY));
            final ConnectionStateListener connectionStateListener = (client, newState) -> {
                if (newState == ConnectionState.CONNECTED) {
                    COUNT_DOWN_LATCH.countDown();
                }
            };
            curatorFramework.getConnectionStateListenable().addListener(connectionStateListener);
            curatorFramework.start();
            try {
                COUNT_DOWN_LATCH.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
        return curatorFramework;
    }

    /**
     * get zk node path.
     * @param rtProperties {@link RtProperties}
     * @return zk node path
     */
    public static String nodePath(RtProperties rtProperties) {
        RtProperties.Zookeeper zookeeper = rtProperties.getZookeeper();
        return ZKPaths.makePath(ZKPaths.makePath(zookeeper.getRootNode(),
                zookeeper.getConfigVersion()), zookeeper.getNode());
    }

    /**
     * zk config info bind to {@link Map}.
     * @param rtProperties {@link RtProperties}
     * @return Map
     */
    @SneakyThrows
    public static Map<Object, Object> genPropertiesMap(RtProperties rtProperties) {

        val curatorFramework = getCuratorFramework(rtProperties);
        String nodePath = nodePath(rtProperties);

        Map<Object, Object> result = Maps.newHashMap();
        if (ConfigFileTypeEnum.PROPERTIES.getValue().equalsIgnoreCase(rtProperties.getConfigType().trim())) {
            result = genPropertiesTypeMap(nodePath, curatorFramework);
        } else if (ConfigFileTypeEnum.JSON.getValue().equalsIgnoreCase(rtProperties.getConfigType().trim())) {
            nodePath = ZKPaths.makePath(nodePath, rtProperties.getZookeeper().getConfigKey());
            String value = getVal(nodePath, curatorFramework);
            result = ExtensionLoader
                .getExtensionLoader(ConfigParser.class)
                .getJoin(rtProperties.getConfigType())
                .doParse(value);
        }

        return result;
    }

  /**
   * get properties type config info from zk.
   * @param nodePath config node path
   * @param curatorFramework {@link CuratorFramework}
   * @return properties type config info
   */
    private static Map<Object, Object> genPropertiesTypeMap(String nodePath, CuratorFramework curatorFramework) {
        try {
            final GetChildrenBuilder childrenBuilder = curatorFramework.getChildren();
            final List<String> children = childrenBuilder.watched().forPath(nodePath);
            val properties = Maps.newHashMap();
            children.forEach(c -> {
                String path = ZKPaths.makePath(nodePath, c);
                final String nodeName = ZKPaths.getNodeFromPath(path);
                String value = getVal(path, curatorFramework);
                properties.put(nodeName, value);
            });
            return properties;
        } catch (Exception e) {
            log.error("get zk configs error, nodePath is {}", nodePath, e);
            return Collections.emptyMap();
        }
    }

    /**
     * get config value.
     * @param path config path
     * @param curatorFramework {@link CuratorFramework}
     * @return config value
     */
    private static String getVal(String path, CuratorFramework curatorFramework) {
        final GetDataBuilder data = curatorFramework.getData();
        String value = "";
        try {
            value = new String(data.watched().forPath(path), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("get zk config value failed, path: {}", path, e);
        }
        return value;
    }
}
