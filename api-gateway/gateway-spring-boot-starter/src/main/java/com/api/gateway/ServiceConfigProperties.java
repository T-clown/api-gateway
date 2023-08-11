package com.api.gateway;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微服务注册网关配置项
 */
@ConfigurationProperties(prefix = "api.gateway.http")
public class ServiceConfigProperties {
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 启动端口号
     */
    private Integer port;
    /**
     * 接口版本号
     */
    private String version;

    private String adminUrl;

    public String getAdminUrl() {
        return adminUrl;
    }

    public void setAdminUrl(String adminUrl) {
        this.adminUrl = adminUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
