package com.api.gateway.mapper;

import com.api.gateway.model.dto.AppPluginDTO;
import com.api.gateway.model.entity.AppPlugin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface AppPluginMapper extends BaseMapper<AppPlugin> {

    List<AppPluginDTO> queryEnabledPlugins(@Param("appIds") List<Integer> appIds);
}
