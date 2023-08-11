package com.api.gateway.chain;

import com.api.gateway.cache.PluginCache;
import com.api.gateway.config.ServerConfigProperties;
import com.api.gateway.plugin.AbstractGatewayPlugin;
import com.api.gateway.plugin.GatewayPlugin;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PluginChain extends AbstractGatewayPlugin {
    /**
     * the pos point to current plugin
     */
    private int pos;
    /**
     * the plugins of chain
     */
    private List<GatewayPlugin> plugins;

    private final String appName;

    public PluginChain(ServerConfigProperties properties, String appName) {
        super(properties);
        this.appName = appName;
    }

    /**
     * add enabled plugin to chain
     *
     * @param gatewayPlugin
     */
    public void addPlugin(GatewayPlugin gatewayPlugin) {
        if (plugins == null) {
            plugins = new ArrayList<>();
        }
        if (!PluginCache.isEnabled(appName, gatewayPlugin.name())) {
            return;
        }
        plugins.add(gatewayPlugin);
        // order by the plugin's order
        plugins.sort(Comparator.comparing(GatewayPlugin::order));
    }

    @Override
    public Integer order() {
        return null;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public Mono<Void> execute(ServerWebExchange exchange, PluginChain pluginChain) {
        if (pos == plugins.size()) {
            return exchange.getResponse().setComplete();
        }
        return pluginChain.plugins.get(pos++).execute(exchange, pluginChain);
    }

    public String getAppName() {
        return appName;
    }

}
