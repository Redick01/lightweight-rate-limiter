package io.redick01.ratelimiter.starter.util;

import com.google.common.collect.Maps;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KeyValue;
import io.redick01.ratelimiter.common.config.RtProperties;
import io.redick01.ratelimiter.common.enums.Singleton;
import io.redick01.ratelimiter.parser.config.ConfigParser;
import io.redick01.spi.ExtensionLoader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * @author Redick01
 */
public class EtcdUtil {

    private EtcdUtil() {}


    public static Client client(RtProperties.Etcd etcd) {
        Client client = Singleton.INST.get(Client.class);
        if (Objects.isNull(client)) {
            if (!etcd.getAuthEnable()) {
                client = Client.builder()
                        .endpoints(etcd.getEndpoints().split(","))
                        .build();
            } else {
                client = Client.builder()
                        .endpoints(etcd.getEndpoints().split(","))
                        .user(ByteSequence.from(etcd.getUser(), Charset.forName(etcd.getCharset())))
                        .password(ByteSequence.from(etcd.getPassword(), Charset.forName(etcd.getCharset())))
                        .authority(etcd.getAuthority())
                        .build();
            }
        }
        return client;
    }

    public static Map<Object, Object> getConfigContent(final RtProperties.Etcd etcd, final String configType) throws ExecutionException
            , InterruptedException, IOException {
        KeyValue keyValue = client(etcd)
                .getKVClient()
                .get(ByteSequence.from(etcd.getKey(), StandardCharsets.UTF_8))
                .get()
                .getKvs()
                .get(0);
        if (Objects.isNull(keyValue)) {
            return Maps.newHashMap();
        }
        return ExtensionLoader
                .getExtensionLoader(ConfigParser.class)
                .getJoin(configType)
                .doParse(keyValue.getValue().toString(Charset.forName(etcd.getCharset())));

    }
}
