package com.api.gateway.service.impl;


import com.api.gateway.mapper.AppInstanceMapper;
import com.api.gateway.mapper.AppMapper;
import com.api.gateway.model.dto.UpdateWeightDTO;
import com.api.gateway.model.entity.App;
import com.api.gateway.model.entity.AppInstance;
import com.api.gateway.model.vo.InstanceVO;
import com.api.gateway.service.AppInstanceService;
import com.api.gateway.transfer.InstanceVOTransfer;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;


@Service
public class AppInstanceServiceImpl implements AppInstanceService {

    @Resource
    private AppInstanceMapper instanceMapper;

    @Resource
    private AppMapper appMapper;

    @Override
    public List<InstanceVO> queryList(Integer appId) {
        App app = appMapper.selectById(appId);
        QueryWrapper<AppInstance> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(AppInstance::getAppId, appId);
        List<AppInstance> instanceList = instanceMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(instanceList)) {
            return Lists.newArrayList();
        }
        List<InstanceVO> voList = InstanceVOTransfer.INSTANCE.mapToVOS(instanceList);
        voList.forEach(vo -> vo.setAppName(app.getAppName()));
        return voList;
    }

    @Override
    public void updateWeight(UpdateWeightDTO updateWeightDTO) {
        AppInstance appInstance = new AppInstance();
        appInstance.setId(updateWeightDTO.getId());
        appInstance.setWeight(updateWeightDTO.getWeight());
        instanceMapper.updateById(appInstance);
    }
}
