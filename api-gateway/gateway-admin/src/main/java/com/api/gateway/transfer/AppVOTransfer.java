package com.api.gateway.transfer;

import com.api.gateway.model.entity.App;
import com.api.gateway.model.vo.AppVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper
public interface AppVOTransfer {

    AppVOTransfer INSTANCE = Mappers.getMapper(AppVOTransfer.class);

    @Mappings({
            @Mapping(target = "createdTime", expression = "java(com.api.gateway.utils.DateUtils.formatToYYYYMMDDHHmmss(app.getCreatedTime()))")
    })
    AppVO mapToVO(App app);

    List<AppVO> mapToVOList(List<App> appList);
}
