package com.api.gateway.service;


import com.api.gateway.model.dto.AppRuleDTO;
import com.api.gateway.model.dto.ChangeStatusDTO;
import com.api.gateway.model.dto.RuleDTO;
import com.api.gateway.model.vo.RuleVO;

import java.util.List;


public interface RuleService {

    List<AppRuleDTO> getEnabledRule();

    void add(RuleDTO ruleDTO);

    void delete(Integer id);

    List<RuleVO> queryList(String appName);

    void changeStatus(ChangeStatusDTO statusDTO);
}
