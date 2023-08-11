package com.api.gateway.transfer;

import com.api.gateway.model.entity.AppInstance;
import com.api.gateway.model.vo.InstanceVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper
public interface InstanceVOTransfer {

    InstanceVOTransfer INSTANCE = Mappers.getMapper(InstanceVOTransfer.class);

    @Mappings({
            @Mapping(target = "createdTime", expression = "java(com.api.gateway.utils.DateUtils.formatToYYYYMMDDHHmmss(appInstance.getCreatedTime()))")
    })
    InstanceVO mapToVO(AppInstance appInstance);

    List<InstanceVO> mapToVOS(List<AppInstance> appInstances);
}
