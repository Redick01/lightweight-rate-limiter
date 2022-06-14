package io.redick01.ratelimiter.starter.refresh;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import io.redick01.ratelimiter.common.config.RtProperties;
import io.redick01.ratelimiter.common.enums.ConfigFileTypeEnum;
import io.redick01.ratelimiter.refresh.AbstractRefresher;
import io.redick01.ratelimiter.starter.util.NacosUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Redick01
 */
@Slf4j
@SuppressWarnings("all")
public class NacosRefresher extends AbstractRefresher implements InitializingBean, Listener {

    private final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    private ConfigFileTypeEnum configFileType;

    @NacosInjected
    private ConfigService configService;

    @Resource
    private RtProperties rtProperties;

    @Resource
    private Environment environment;


    @Override
    public void afterPropertiesSet() throws Exception {
        configFileType = NacosUtil.getConfigFileType(rtProperties);
        String dataId = NacosUtil.getDataId(rtProperties.getNacos(), environment, configFileType);
        String group = NacosUtil.getGroup(rtProperties.getNacos());
        try {
            configService.addListener(dataId, group, this);
            log.info("add nacos listener success, dataId: {}, group: {}", dataId, group);
        } catch (NacosException e) {
            log.error("add nacos listener failed, dataId: {}, group: {}", dataId, group, e);
        }
    }

    @Override
    public Executor getExecutor() {
        return EXECUTOR;
    }

    @Override
    public void receiveConfigInfo(String s) {
        refresh(s, configFileType);
    }
}