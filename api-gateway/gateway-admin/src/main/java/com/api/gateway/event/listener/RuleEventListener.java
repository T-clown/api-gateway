package com.api.gateway.event.listener;

import com.api.gateway.event.RuleAddEvent;
import com.api.gateway.event.RuleDeleteEvent;
import com.api.gateway.sync.RouteRuleConfigPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;


@Component
public class RuleEventListener {

    @Resource
    private RouteRuleConfigPublisher configPublisher;

    @EventListener
    public void onAdd(RuleAddEvent ruleAddEvent) {
        configPublisher.publishRouteRuleConfig();
    }

    @EventListener
    public void onDelete(RuleDeleteEvent ruleDeleteEvent) {
        configPublisher.publishRouteRuleConfig();
    }
}
