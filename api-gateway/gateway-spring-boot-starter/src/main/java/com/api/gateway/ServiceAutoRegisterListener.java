package com.api.gateway;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.api.gateway.constants.AdminConstants;
import com.api.gateway.constants.NacosConstants;
import com.api.gateway.constants.GatewayCodeEnum;
import com.api.gateway.exception.GatewayException;
import com.api.gateway.model.dto.RegisterAppDTO;
import com.api.gateway.model.dto.UnregisterAppDTO;
import com.api.gateway.utils.IpUtil;
import com.api.gateway.utils.OkhttpTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 服务启动完成后注册到注册中心
 */
public class ServiceAutoRegisterListener implements ApplicationListener<ContextRefreshedEvent> {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServiceAutoRegisterListener.class);

    private volatile AtomicBoolean registered = new AtomicBoolean(false);

    private final ServiceConfigProperties properties;

    /**
     * 无法通过@NacosInjected注入，why
     */
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

    private final ExecutorService pool;

    private static List<String> ignoreUrlList = new LinkedList<>();

    static {
        ignoreUrlList.add("/error");
    }

    public ServiceAutoRegisterListener(ServiceConfigProperties properties) {
        if (!check(properties)) {
            LOGGER.error("service config port,contextPath,appName adminUrl and version can't be null!");
            throw new GatewayException("service config port,contextPath,appName adminUrl and version can't be empty!");
        }
        this.properties = properties;
        pool = new ThreadPoolExecutor(1, 5, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r);
            }
        });
    }

    /**
     * 检查配置
     *
     * @param properties
     * @return
     */
    private boolean check(ServiceConfigProperties properties) {
        return properties.getPort() != null
                && properties.getVersion() != null
                && properties.getAppName() != null
                && properties.getAdminUrl() != null;
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!registered.compareAndSet(false, true)) {
            return;
        }
        doRegister();
        registerShutDownHook();
    }

    /**
     * send unregister request to admin when jvm shutdown
     */
    private void registerShutDownHook() {
        final String url = "http://" + properties.getAdminUrl() + AdminConstants.UNREGISTER_PATH;
        final UnregisterAppDTO unregisterAppDTO = new UnregisterAppDTO();
        unregisterAppDTO.setAppName(properties.getAppName());
        unregisterAppDTO.setVersion(properties.getVersion());
        unregisterAppDTO.setIp(IpUtil.getLocalIpAddress());
        unregisterAppDTO.setPort(properties.getPort());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            OkhttpTool.doPost(url, unregisterAppDTO);
            LOGGER.info("[{}:{}] unregister from gateway-admin success!", unregisterAppDTO.getAppName(), unregisterAppDTO.getVersion());
        }));
    }

    /**
     * register all interface info to register center
     */
    private void doRegister() {
        Instance instance = new Instance();
        instance.setIp(IpUtil.getLocalIpAddress());
        instance.setPort(properties.getPort());
        instance.setEphemeral(true);
        Map<String, String> metadataMap = new HashMap<>();
        metadataMap.put("version", properties.getVersion());
        metadataMap.put("appName", properties.getAppName());
        instance.setMetadata(metadataMap);
        try {
            namingService.registerInstance(properties.getAppName(), NacosConstants.APP_GROUP_NAME, instance);

        } catch (NacosException e) {
            LOGGER.error("register to nacos fail", e);
            throw new GatewayException(e.getErrCode(), e.getErrMsg());
        }
        LOGGER.info("register service [{}] success",properties.getAppName());
        // send register request to gateway-admin
        String url = "http://" + properties.getAdminUrl() + AdminConstants.REGISTER_PATH;
        RegisterAppDTO registerAppDTO = buildRegisterAppDTO(instance);
        OkhttpTool.doPost(url, registerAppDTO);
        LOGGER.info("register to gateway-admin success!");
    }


    private RegisterAppDTO buildRegisterAppDTO(Instance instance) {
        RegisterAppDTO registerAppDTO = new RegisterAppDTO();
        registerAppDTO.setAppName(properties.getAppName());
        registerAppDTO.setContextPath("/" + properties.getAppName());
        registerAppDTO.setIp(instance.getIp());
        registerAppDTO.setPort(instance.getPort());
        registerAppDTO.setVersion(properties.getVersion());
        return registerAppDTO;
    }
}
