package com.api.gateway.sync;


import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.api.gateway.cache.RouteRuleCache;
import com.api.gateway.config.ServerConfigProperties;
import com.api.gateway.constants.NacosConstants;
import com.api.gateway.constants.GatewayCodeEnum;
import com.api.gateway.exception.GatewayException;
import com.api.gateway.model.dto.AppRuleDTO;
import com.api.gateway.utils.GsonUtils;
import com.api.gateway.utils.IpUtil;
import com.api.gateway.utils.GatewayThreadFactory;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Configuration
public class DataSyncTaskListener implements ApplicationListener<ContextRefreshedEvent> {

    private final static Logger LOGGER = LoggerFactory.getLogger(DataSyncTaskListener.class);

    private static final ScheduledThreadPoolExecutor SCHEDULED_POOL = new ScheduledThreadPoolExecutor(1, new GatewayThreadFactory("service-sync", true).create());

    private NamingService namingService;

    @Value("${nacos.discovery.server-addr}")
    private String baseUrl;

    @PostConstruct
    public void init() {
        try {
            namingService = NacosFactory.createNamingService(baseUrl);
            configService = NacosFactory.createConfigService(baseUrl);
        } catch (NacosException e) {
            throw new GatewayException(GatewayCodeEnum.CONNECT_NACOS_ERROR);
        }
    }


    @Autowired
    private ServerConfigProperties properties;

    private ConfigService configService;

    private Environment environment;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() != null) {
            return;
        }
        environment = event.getApplicationContext().getEnvironment();
        SCHEDULED_POOL.scheduleWithFixedDelay(new DataSyncTask(namingService)
                , 0L, properties.getCacheRefreshInterval(), TimeUnit.SECONDS);
        registryMyself();
        initConfig();
    }

    private void registryMyself() {
        Instance instance = new Instance();
        instance.setIp(IpUtil.getLocalIpAddress());
        String port = environment.getProperty("server.port");

        Assert.isTrue(NumberUtils.isParsable(port), "端口号错误");

        instance.setPort(Integer.parseInt(port));
        try {
            if (namingService != null) {
                namingService.registerInstance("gateway-server", NacosConstants.APP_GROUP_NAME, instance);
            }
        } catch (NacosException e) {
            throw new GatewayException(GatewayCodeEnum.CONNECT_NACOS_ERROR);
        }
    }

    private void initConfig() {
        try {
            String serverAddr = environment.getProperty("nacos.discovery.server-addr");
            Assert.hasText(serverAddr, "nacos server addr is missing");
            configService = NacosFactory.createConfigService(serverAddr);
            // pull config in first time
            String config = configService.getConfig(NacosConstants.DATA_ID_NAME, NacosConstants.APP_GROUP_NAME, 5000);
            DataSyncTaskListener.updateConfig(config);
            // add config listener
            configService.addListener(NacosConstants.DATA_ID_NAME, NacosConstants.APP_GROUP_NAME, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    LOGGER.info("receive config info:\n{}", configInfo);
                    DataSyncTaskListener.updateConfig(configInfo);
                }
            });
        } catch (NacosException e) {
            throw new GatewayException(GatewayCodeEnum.CONNECT_NACOS_ERROR);
        }
    }


    public static void updateConfig(String configInfo) {
        if (StringUtils.isBlank(configInfo)) {
            return;
        }
        List<AppRuleDTO> list = GsonUtils.fromJson(configInfo, new TypeToken<List<AppRuleDTO>>() {
        }.getType());
        Map<String, List<AppRuleDTO>> map = list.stream().collect(Collectors.groupingBy(AppRuleDTO::getAppName));
        RouteRuleCache.add(map);
        LOGGER.info("update route rule cache success");
    }

}
