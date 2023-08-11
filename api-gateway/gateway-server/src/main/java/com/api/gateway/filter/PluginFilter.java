package com.api.gateway.filter;

import com.api.gateway.cache.ServiceCache;
import com.api.gateway.chain.PluginChain;
import com.api.gateway.config.ServerConfigProperties;
import com.api.gateway.constants.GatewayCodeEnum;
import com.api.gateway.exception.GatewayException;
import com.api.gateway.plugin.impl.AuthPlugin;
import com.api.gateway.plugin.impl.DynamicRoutePlugin;
import com.api.gateway.plugin.impl.RateLimitPlugin;
import org.springframework.http.server.RequestPath;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;


public class PluginFilter implements WebFilter {

    private final ServerConfigProperties properties;

    public PluginFilter(ServerConfigProperties properties) {
        this.properties = properties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String appName = parseAppName(exchange);
        if (CollectionUtils.isEmpty(ServiceCache.getAllInstances(appName))) {
            throw new GatewayException(GatewayCodeEnum.SERVICE_NOT_FIND);
        }
        PluginChain pluginChain = new PluginChain(properties, appName);
        pluginChain.addPlugin(new DynamicRoutePlugin(properties));
        pluginChain.addPlugin(new AuthPlugin(properties));
        pluginChain.addPlugin(new RateLimitPlugin(properties));
        return pluginChain.execute(exchange, pluginChain);
    }

    private String parseAppName(ServerWebExchange exchange) {
        RequestPath path = exchange.getRequest().getPath();
        String appName = path.value().split("/")[1];
        return appName;
    }
}
