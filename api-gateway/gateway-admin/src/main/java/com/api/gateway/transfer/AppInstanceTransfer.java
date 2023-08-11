package com.api.gateway.transfer;

import com.api.gateway.model.dto.ServiceInstance;
import com.api.gateway.model.entity.AppInstance;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper
public interface AppInstanceTransfer {

    AppInstanceTransfer INSTANCE = Mappers.getMapper(AppInstanceTransfer.class);

    ServiceInstance mapToService(AppInstance appInstance);

    List<ServiceInstance> mapToServiceList(List<AppInstance> appInstances);
}
