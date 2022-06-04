package io.redick01.ratelimiter.starter.util;

import com.google.common.collect.Maps;
import io.redick01.ratelimiter.common.config.RtProperties;
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
import static io.redick01.ratelimiter.common.enums.ConfigFileTypeEnum.PROPERTIES;
import static io.redick01.ratelimiter.common.enums.ConfigFileTypeEnum.JSON;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author Redick01
 */
@Slf4j
public class CuratorUtil {

    private static CuratorFramework curatorFramework;

    private static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);

    private CuratorUtil() {}

    public static CuratorFramework getCuratorFramework(RtProperties rtProperties) {
        if (curatorFramework == null) {
            RtProperties.Zookeeper zookeeper = rtProperties.getZookeeper();
            curatorFramework = CuratorFrameworkFactory.newClient(zookeeper.getZkConnectStr(),
                    new ExponentialBackoffRetry(1000, 3));
            final ConnectionStateListener connectionStateListener = (client, newState) -> {
                if (newState == ConnectionState.CONNECTED) {
                    COUNT_DOWN_LATCH.countDown();
                }};
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

    public static String nodePath(RtProperties rtProperties) {
        RtProperties.Zookeeper zookeeper = rtProperties.getZookeeper();
        return ZKPaths.makePath(ZKPaths.makePath(zookeeper.getRootNode(),
                zookeeper.getConfigVersion()), zookeeper.getNode());
    }

    @SneakyThrows
    public static Map<Object, Object> genPropertiesMap(RtProperties rtProperties) {

        val curatorFramework = getCuratorFramework(rtProperties);
        String nodePath = nodePath(rtProperties);

        Map<Object, Object> result = Maps.newHashMap();
        if (PROPERTIES.getValue().equalsIgnoreCase(rtProperties.getConfigType().trim())) {
            result = genPropertiesTypeMap(nodePath, curatorFramework);
        } else if (JSON.getValue().equalsIgnoreCase(rtProperties.getConfigType().trim())) {
            nodePath = ZKPaths.makePath(nodePath, rtProperties.getZookeeper().getConfigKey());
            String value = getVal(nodePath, curatorFramework);
            result = ExtensionLoader.getExtensionLoader(ConfigParser.class).getJoin(rtProperties.getConfigType()).doParse(value);
        }

        return result;
    }

    private static Map<Object, Object> genPropertiesTypeMap(String nodePath, CuratorFramework curatorFramework) {
        try {
            final GetChildrenBuilder childrenBuilder = curatorFramework.getChildren();
            final List<String> children = childrenBuilder.watched().forPath(nodePath);
            Map<Object, Object> properties = Maps.newHashMap();
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
