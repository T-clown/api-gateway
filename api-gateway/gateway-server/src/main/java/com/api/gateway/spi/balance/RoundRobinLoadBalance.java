package com.api.gateway.spi.balance;


import com.api.gateway.annotation.LoadBalanceAno;
import com.api.gateway.model.dto.ServiceInstance;
import com.api.gateway.spi.AbstractLoadBalance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 轮询算法
 */
@LoadBalanceAno(RoundRobinLoadBalance.NAME)
public class RoundRobinLoadBalance extends AbstractLoadBalance {

    public static final String NAME = "ROUND_ROBIN";

    @Override
    public ServiceInstance doSelect(List<ServiceInstance> instances) {
        return instances.get(ThreadLocalRandom.current().nextInt(instances.size()));
    }
}
