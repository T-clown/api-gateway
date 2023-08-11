package com.api.gateway.service;


import com.api.gateway.model.dto.UpdateWeightDTO;
import com.api.gateway.model.vo.InstanceVO;

import java.util.List;


public interface AppInstanceService {
    /**
     * query instances by appId
     * @param appId
     * @return
     */
    List<InstanceVO> queryList(Integer appId);

    void updateWeight(UpdateWeightDTO updateWeightDTO);
}
