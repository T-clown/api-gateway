package com.api.gateway.service;


import com.api.gateway.model.dto.AppInfoDTO;
import com.api.gateway.model.dto.ChangeStatusDTO;
import com.api.gateway.model.dto.RegisterAppDTO;
import com.api.gateway.model.dto.UnregisterAppDTO;
import com.api.gateway.model.vo.AppVO;

import java.util.List;


public interface AppService {

    /**
     * register app
     *
     * @param registerAppDTO
     */
    void register(RegisterAppDTO registerAppDTO);

    /**
     * unregister app instance
     *
     * @param unregisterAppDTO
     */
    void unregister(UnregisterAppDTO unregisterAppDTO);

    /**
     * get app infos by appNames
     * @param appNames
     * @return
     */
    List<AppInfoDTO> getAppInfos(List<String> appNames);

    List<AppVO> getList();

    void updateEnabled(ChangeStatusDTO statusDTO);

    void delete(Integer id);
}
