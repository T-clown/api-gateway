package com.api.gateway.plugin;


import com.api.gateway.chain.PluginChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


public interface GatewayPlugin {
    /**
     * lower values have higher priority
     *
     * @return
     */
    Integer order();

    /**
     * return current plugin name
     *
     * @return
     */
    String name();

    /**
     *
     * @param exchange
     * @param pluginChain
     * @return
     */
    Mono<Void> execute(ServerWebExchange exchange, PluginChain pluginChain);

}
