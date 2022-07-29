package io.redick01.ratelimiter.starter.refresh;

import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.watch.WatchEvent;
import io.etcd.jetcd.watch.WatchResponse;
import io.redick01.ratelimiter.common.config.RtProperties;
import io.redick01.ratelimiter.common.util.PropertiesBinder;
import io.redick01.ratelimiter.parser.config.ConfigParser;
import io.redick01.spi.ExtensionLoader;
import java.nio.charset.Charset;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * @author Redick01
 */
@Slf4j
public class EtcdListener implements Watch.Listener {

    private final RtProperties rtProperties;

    private final String key;

    private final EtcdRefresher etcdRefresher;

    public EtcdListener(RtProperties rtProperties, String key, EtcdRefresher etcdRefresher) {
        this.rtProperties = rtProperties;
        this.key = key;
        this.etcdRefresher = etcdRefresher;
    }

    @SneakyThrows
    @Override
    public void onNext(WatchResponse response) {
        log.info("etcd config content updated, key is " + key);
        KeyValue keyValue = response.getEvents().get(0).getKeyValue();
        WatchEvent.EventType eventType = response.getEvents().get(0).getEventType();
        if (WatchEvent.EventType.PUT.equals(eventType)) {
            log.info("the etcd config content should be updated, key is " + key);
            String configType = rtProperties.getConfigType();
            val properties = ExtensionLoader
                .getExtensionLoader(ConfigParser.class)
                .getJoin(configType)
                .doParse(keyValue.getValue().toString(Charset.forName(rtProperties.getEtcd().getCharset())));
            PropertiesBinder.bindRtProperties(properties, rtProperties);
            etcdRefresher.doRefresh(rtProperties);
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
}
