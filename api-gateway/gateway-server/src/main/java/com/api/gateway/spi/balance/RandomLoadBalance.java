package com.api.gateway.spi.balance;


import com.api.gateway.annotation.LoadBalanceAno;
import com.api.gateway.model.dto.ServiceInstance;
import com.api.gateway.spi.AbstractLoadBalance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机算法
 */
@LoadBalanceAno(RandomLoadBalance.NAME)
public class RandomLoadBalance extends AbstractLoadBalance {

    public static final String NAME = "RANDOM";

    @Override
    public ServiceInstance doSelect(List<ServiceInstance> instances) {
        return instances.get(ThreadLocalRandom.current().nextInt(instances.size()));
    }
}
