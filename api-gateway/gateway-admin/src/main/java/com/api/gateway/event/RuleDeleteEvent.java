package com.api.gateway.event;

import com.api.gateway.model.dto.AppRuleDTO;
import org.springframework.context.ApplicationEvent;

public class RuleDeleteEvent extends ApplicationEvent {

    private AppRuleDTO appRuleDTO;

    public RuleDeleteEvent(Object source, AppRuleDTO appRuleDTO) {
        super(source);
        this.appRuleDTO = appRuleDTO;
    }

    public AppRuleDTO getAppRuleDTO() {
        return appRuleDTO;
    }
}
