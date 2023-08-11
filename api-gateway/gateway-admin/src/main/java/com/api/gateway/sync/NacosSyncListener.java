package com.api.gateway.sync;


import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.api.gateway.constants.EnabledEnum;
import com.api.gateway.constants.NacosConstants;
import com.api.gateway.constants.GatewayCodeEnum;
import com.api.gateway.exception.GatewayException;
import com.api.gateway.model.NacosMetadata;
import com.api.gateway.model.dto.AppInfoDTO;
import com.api.gateway.model.dto.ServiceInstance;
import com.api.gateway.service.AppService;
import com.api.gateway.utils.GsonUtils;
import com.api.gateway.utils.GatewayThreadFactory;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Configuration
public class NacosSyncListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NacosSyncListener.class);

    private static ScheduledThreadPoolExecutor scheduledPool = new ScheduledThreadPoolExecutor(1,
            new GatewayThreadFactory("nacos-sync", true).create());

    //@NacosInjected
    private NamingService namingService;

    @Value("${nacos.discovery.server-addr}")
    private String baseUrl;
    @PostConstruct
    public void init() {
        try {
            namingService = NacosFactory.createNamingService(baseUrl);
        } catch (NacosException e) {
            throw new GatewayException(GatewayCodeEnum.CONNECT_NACOS_ERROR);
        }
    }

    @Resource
    private AppService appService;

    @Resource
    private RouteRuleConfigPublisher routeRuleConfigPublisher;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() != null) {
            return;
        }
        scheduledPool.scheduleWithFixedDelay(new NacosSyncTask(namingService, appService), 0, 30L, TimeUnit.SECONDS);
        routeRuleConfigPublisher.publishRouteRuleConfig();
        LOGGER.info("NacosSyncListener init success.");
    }


    static class NacosSyncTask implements Runnable {

        private NamingService namingService;

        private AppService appService;

        public NacosSyncTask(NamingService namingService, AppService appService) {
            this.namingService = namingService;
            this.appService = appService;
        }

        /**
         * Regular update weight,enabled plugins,enabled status to nacos instance
         */
        @Override
        public void run() {
            try {
                // get all app names
                ListView<String> services = namingService.getServicesOfServer(1, Integer.MAX_VALUE, NacosConstants.APP_GROUP_NAME);
                if (CollectionUtils.isEmpty(services.getData())) {
                    return;
                }
                List<String> appNames = services.getData();
                List<AppInfoDTO> appInfos = appService.getAppInfos(appNames);
                for (AppInfoDTO appInfo : appInfos) {
                    if (CollectionUtils.isEmpty(appInfo.getInstances())) {
                        continue;
                    }
                    for (ServiceInstance serviceInstance : appInfo.getInstances()) {
                        Instance instance = buildInstance(appInfo, serviceInstance);
                        namingService.registerInstance(appInfo.getAppName(), NacosConstants.APP_GROUP_NAME, instance);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("nacos sync task error", e);
            }
        }

        private Instance buildInstance(AppInfoDTO appInfo, ServiceInstance serviceInstance) {
            Instance instance = new Instance();
            instance.setIp(serviceInstance.getIp());
            instance.setPort(serviceInstance.getPort());
            instance.setWeight(serviceInstance.getWeight().doubleValue());
            instance.setEphemeral(true);
            instance.setEnabled(EnabledEnum.ENABLE.getCode().equals(appInfo.getEnabled()));
            NacosMetadata metadata = new NacosMetadata();
            metadata.setAppName(appInfo.getAppName());
            metadata.setVersion(serviceInstance.getVersion());
            metadata.setPlugins(String.join(",", appInfo.getEnabledPlugins()));
            Map<String, String> instanceMeta = GsonUtils.fromJson(GsonUtils.toJson(metadata), Map.class);
            instance.setMetadata(instanceMeta);
            return instance;
        }
    }
}
