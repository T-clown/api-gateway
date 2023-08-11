package com.api.gateway.plugin.impl;

import com.api.gateway.chain.PluginChain;
import com.api.gateway.config.ServerConfigProperties;
import com.api.gateway.constants.PluginEnum;
import com.api.gateway.plugin.AbstractGatewayPlugin;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 授权插件
 */
public class AuthPlugin extends AbstractGatewayPlugin {

    public AuthPlugin(ServerConfigProperties properties) {
        super(properties);
    }

    @Override
    public Integer order() {
        return PluginEnum.AUTH.getOrder();
    }

    @Override
    public String name() {
        return PluginEnum.AUTH.getName();
    }

    @Override
    public Mono<Void> execute(ServerWebExchange exchange, PluginChain pluginChain) {
        System.out.println("auth plugin");
        return pluginChain.execute(exchange, pluginChain);
    }
}
