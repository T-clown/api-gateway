package com.api.gateway.plugin.impl;


import com.alibaba.csp.sentinel.SphO;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.api.gateway.chain.PluginChain;
import com.api.gateway.config.ServerConfigProperties;
import com.api.gateway.constants.ServerConstants;
import com.api.gateway.constants.GatewayCodeEnum;
import com.api.gateway.constants.PluginEnum;
import com.api.gateway.exception.GatewayException;
import com.api.gateway.plugin.AbstractGatewayPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 限流插件
 */
public class RateLimitPlugin extends AbstractGatewayPlugin {

    private final Logger logger = LoggerFactory.getLogger(RateLimitPlugin.class);

    private static Map<String, Integer> rateLimitTypeMap = new HashMap<>();

    static {
        rateLimitTypeMap.put(ServerConstants.LIMIT_BY_QPS, RuleConstant.FLOW_GRADE_QPS);
        rateLimitTypeMap.put(ServerConstants.LIMIT_BY_THREAD, RuleConstant.FLOW_GRADE_THREAD);
    }

    public RateLimitPlugin(ServerConfigProperties properties) {
        super(properties);
    }

    @Override
    public Integer order() {
        return PluginEnum.RATE_LIMIT.getOrder();
    }

    @Override
    public String name() {
        return PluginEnum.RATE_LIMIT.getName();
    }

    @Override
    public Mono<Void> execute(ServerWebExchange exchange, PluginChain pluginChain) {
        String appName = pluginChain.getAppName();
        initFlowRules(appName);
        if (SphO.entry(appName)) {
            // 务必保证finally会被执行
            try {
                /**
                 * 被保护的业务逻辑
                 */
                return pluginChain.execute(exchange, pluginChain);
            } finally {
                SphO.exit();
            }
        }
        throw new GatewayException(GatewayCodeEnum.REQUEST_LIMIT_ERROR);
    }

    private void initFlowRules(String resource) {
        Assert.hasText(properties.getRateLimitType(), "config ship.gate.rateLimitType required!");
        Assert.notNull(properties.getRateLimitCount(), "config ship.gate.rateLimitCount required!");
        List<FlowRule> list = new ArrayList<>();
        FlowRule flowRule = new FlowRule();
        flowRule.setResource(resource);
        flowRule.setGrade(rateLimitTypeMap.get(properties.getRateLimitType()));
        flowRule.setCount(properties.getRateLimitCount().doubleValue());
        list.add(flowRule);
        FlowRuleManager.loadRules(list);
    }
}
