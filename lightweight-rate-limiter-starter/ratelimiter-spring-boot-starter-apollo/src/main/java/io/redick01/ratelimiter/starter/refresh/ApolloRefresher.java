package io.redick01.ratelimiter.starter.refresh;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import io.redick01.ratelimiter.common.config.RtProperties;
import io.redick01.ratelimiter.common.enums.ConfigFileTypeEnum;
import io.redick01.ratelimiter.refresh.AbstractRefresher;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author Redick01
 */
@Slf4j
public class ApolloRefresher extends AbstractRefresher implements InitializingBean, ConfigChangeListener {

    @Value("${apollo.bootstrap.namespaces:application}")
    private String namespace;

    @Resource
    private RtProperties rtProperties;

    private ConfigFileTypeEnum configFileTypeEnum;

    @Override
    public void onChange(ConfigChangeEvent configChangeEvent) {
        log.info("config file {} changed.", configChangeEvent.getNamespace());
        ConfigFile configFile = ConfigService.getConfigFile(namespace,
                ConfigFileFormat.fromString(configFileTypeEnum.getValue()));
        refresh(configFile.getContent(), configFileTypeEnum);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String configNamespace = namespace.split(",")[0];
        if (Objects.nonNull(rtProperties) && Objects.nonNull(rtProperties.getApollo())
                && StringUtils.isNotBlank(rtProperties.getApollo().getNamespace())) {
            configNamespace = rtProperties.getApollo().getNamespace();
        }
        Config config = ConfigService.getConfig(configNamespace);
        ConfigFileFormat configFileFormat = configFileFormat(configNamespace);
        namespace = configNamespace.replaceAll("." + configFileFormat.getValue(), "");
        configFileTypeEnum = ConfigFileTypeEnum.of(configFileFormat.getValue());
        config.addChangeListener(this);
    }

    private ConfigFileFormat configFileFormat(final String namespace) {
        ConfigFileFormat configFileFormat = ConfigFileFormat.Properties;
        if (namespace.contains(ConfigFileFormat.YAML.getValue())) {
            configFileFormat = ConfigFileFormat.YAML;
        } else if (namespace.contains(ConfigFileFormat.YML.getValue())) {
            configFileFormat = ConfigFileFormat.YML;
        } else if (namespace.contains(ConfigFileFormat.JSON.getValue())) {
            configFileFormat = ConfigFileFormat.JSON;
        } else if (namespace.contains(ConfigFileFormat.XML.getValue())) {
            configFileFormat = ConfigFileFormat.XML;
        } else if (namespace.contains(ConfigFileFormat.TXT.getValue())) {
            configFileFormat = ConfigFileFormat.TXT;
        }
        return configFileFormat;
    }
}
