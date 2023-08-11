package com.api.gateway.spi;


import com.api.gateway.model.dto.ServiceInstance;

import java.util.List;


public interface LoadBalance {

    ServiceInstance select(List<ServiceInstance> instances);
}
