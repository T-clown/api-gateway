
package com.api.gateway.spi;

import com.api.gateway.model.dto.ServiceInstance;
import org.springframework.util.CollectionUtils;

import java.util.List;

public abstract class AbstractLoadBalance implements LoadBalance {

    /**
     * Calculate the weight according to the uptime proportion of warmup time
     * the new weight will be within 1(inclusive) to weight(inclusive)
     *
     * @param uptime the uptime in milliseconds
     * @param warmup the warmup time in milliseconds
     * @param weight the weight of an invoker
     * @return weight which takes warmup into account
     */
    static int calculateWarmupWeight(int uptime, int warmup, int weight) {
        int ww = (int) (uptime / ((float) warmup / weight));
        return ww < 1 ? 1 : (Math.min(ww, weight));
    }

    @Override
    public ServiceInstance select(List<ServiceInstance> instances) {
        if (CollectionUtils.isEmpty(instances)) {
            return null;
        }
        if (instances.size() == 1) {
            return instances.get(0);
        }
        return doSelect(instances);
    }

    protected abstract ServiceInstance doSelect(List<ServiceInstance> instances);

}
