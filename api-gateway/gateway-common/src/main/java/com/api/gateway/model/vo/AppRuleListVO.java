package com.api.gateway.model.vo;

import com.api.gateway.model.dto.AppRuleDTO;

import java.util.List;


public class AppRuleListVO {

    private List<AppRuleDTO> list;

    public List<AppRuleDTO> getList() {
        return list;
    }

    public void setList(List<AppRuleDTO> list) {
        this.list = list;
    }
}
