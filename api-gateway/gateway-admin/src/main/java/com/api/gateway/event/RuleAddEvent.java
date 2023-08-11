package com.api.gateway.event;

import com.api.gateway.model.dto.AppRuleDTO;
import org.springframework.context.ApplicationEvent;


public class RuleAddEvent extends ApplicationEvent {

    private AppRuleDTO appRuleDTO;

    public RuleAddEvent(Object source, AppRuleDTO appRuleDTO) {
        super(source);
        this.appRuleDTO = appRuleDTO;
    }

    public AppRuleDTO getAppRuleDTO() {
        return appRuleDTO;
    }
}
