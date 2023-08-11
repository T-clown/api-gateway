package com.api.gateway;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 微服务项目引入此starter，并配置好以下参数，服务启动完成会注册服务到注册中心
 * @author t
 * @see  ServiceConfigProperties
 * 注意：引用的项目需要配置包扫描到此类
 */
@Configuration
@Import(value = {ServiceAutoRegisterListener.class})
@EnableConfigurationProperties(value = {ServiceConfigProperties.class})
public class ServiceAutoRegistryConfiguration {

}
