package com.api.gateway.transfer;


import com.api.gateway.model.dto.AppRuleDTO;
import com.api.gateway.model.entity.RouteRule;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper
public interface AppRuleVOTransfer {

    AppRuleVOTransfer INSTANCE = Mappers.getMapper(AppRuleVOTransfer.class);

    AppRuleDTO mapToVO(RouteRule routeRule);

    List<AppRuleDTO> mapToVOList(List<RouteRule> routeRules);
}
