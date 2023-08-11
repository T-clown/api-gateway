package com.api.gateway.spi.balance;


import com.api.gateway.annotation.LoadBalanceAno;
import com.api.gateway.model.dto.ServiceInstance;
import com.api.gateway.spi.AbstractLoadBalance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 最少活跃
 */
@LoadBalanceAno(LeastActiveLoadBalance.NAME)
public class LeastActiveLoadBalance extends AbstractLoadBalance {

    public static final String NAME = "LEAST_ACTIVE";

    @Override
    public ServiceInstance doSelect(List<ServiceInstance> instances) {
        return instances.get(ThreadLocalRandom.current().nextInt(instances.size()));
    }
}
