package com.api.gateway.config;

import com.api.gateway.filter.PluginFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.reactive.config.EnableWebFlux;

@Configuration
@EnableWebFlux
//把ServerConfigProperties注册到Spring中，跟在上面加@Component注解一样的功能
@EnableConfigurationProperties(ServerConfigProperties.class)
public class WebConfig {

    @Bean
    public PluginFilter pluginFilter(@Autowired ServerConfigProperties properties) {
        return new PluginFilter(properties);
    }

    /**
     * set order -2 to before DefaultErrorWebExceptionHandler(-1) ResponseStatusExceptionHandler(0)
     *
     * @return
     */
    @Order(-2)
    @Bean
    public GatewayExceptionHandler shipExceptionHandler() {
        return new GatewayExceptionHandler();
    }

}
