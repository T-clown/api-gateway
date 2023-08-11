package com.api.gateway.plugin;


import com.api.gateway.config.ServerConfigProperties;


public abstract class AbstractGatewayPlugin implements GatewayPlugin {

    protected ServerConfigProperties properties;

    public AbstractGatewayPlugin(ServerConfigProperties properties) {
        this.properties = properties;
    }
}
