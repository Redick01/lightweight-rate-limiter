package io.redick01.ratelimiter.starter.refresh;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.watch.WatchEvent;
import io.etcd.jetcd.watch.WatchResponse;
import io.redick01.ratelimiter.common.config.RtProperties;
import io.redick01.ratelimiter.common.util.PropertiesBinder;
import io.redick01.ratelimiter.parser.config.ConfigParser;
import io.redick01.ratelimiter.refresh.AbstractRefresher;

import io.redick01.ratelimiter.starter.util.EtcdUtil;
import io.redick01.spi.ExtensionLoader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author Redick01
 */
@Slf4j
public class EtcdRefresher extends AbstractRefresher implements InitializingBean, Ordered {

    @Resource
    private RtProperties rtProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        RtProperties.Etcd etcd = rtProperties.getEtcd();
        Client client = EtcdUtil.client(etcd);
        String key = etcd.getKey();
        if (StringUtils.isNotBlank(key)) {
            loadConfig(client, key, etcd);
            initWatcher(client, key, etcd);
        } else {
            log.debug("rate limiter key is null, etcd client closed !");
            client.close();
        }

    }

    private void loadConfig(final Client client, final String key, final RtProperties.Etcd etcd) {
        client.getKVClient().get(ByteSequence.from(key, StandardCharsets.UTF_8))
                .whenCompleteAsync((response, exception) -> {
                    // bind config content
                    KeyValue keyValue = response.getKvs().get(0);
                    String configType = rtProperties.getConfigType();
                    try {
                        Map<Object, Object> properties = ExtensionLoader
                                .getExtensionLoader(ConfigParser.class)
                                .getJoin(configType)
                                .doParse(keyValue.getValue().toString(Charset.forName(etcd.getCharset())));
                        PropertiesBinder.bindDtpProperties(properties, rtProperties);
                    } catch (IOException e) {
                        log.error("etcd config content parse failed !", e);
                    }
                })
                .exceptionally(e -> {
                    log.error("load config from etcd exception !", e);
                    return null;
                });
    }

    private void initWatcher(final Client client, final String key, final RtProperties.Etcd etcd) {

        client.getWatchClient().watch(ByteSequence.from(key, StandardCharsets.UTF_8), new Watch.Listener() {
            @SneakyThrows
            @Override
            public void onNext(WatchResponse response) {
                log.info("etcd config content updated, key is " + key);
                KeyValue keyValue = response.getEvents().get(0).getKeyValue();
                WatchEvent.EventType eventType = response.getEvents().get(0).getEventType();
                if (WatchEvent.EventType.PUT.equals(eventType)) {
                    log.info("the etcd config content should be updated, key is " + key);
                    String configType = rtProperties.getConfigType();
                    Map<Object, Object> properties = ExtensionLoader
                            .getExtensionLoader(ConfigParser.class)
                            .getJoin(configType)
                            .doParse(keyValue.getValue().toString(Charset.forName(etcd.getCharset())));
                    doRefresh(properties);
                } else {
                    log.info("the etcd config content should not be updated, key is " + key);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("etcd config watcher exception !", throwable);
            }

            @Override
            public void onCompleted() {
                log.info("etcd config key refreshed, config key is : " + key);
            }
        });
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
