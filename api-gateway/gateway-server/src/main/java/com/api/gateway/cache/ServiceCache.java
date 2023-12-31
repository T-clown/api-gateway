package com.api.gateway.cache;

import com.api.gateway.model.dto.ServiceInstance;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceCache {
    /**
     * key: serviceName
     */
    private static final Map<String, List<ServiceInstance>> SERVICE_MAP = new ConcurrentHashMap<>();

    /**
     * get all instances by serviceName
     *
     * @param serviceName
     * @return
     */
    public static List<ServiceInstance> getAllInstances(String serviceName) {
        return SERVICE_MAP.get(serviceName);
    }

    /**
     * add service to cache
     * @param serviceName
     * @param list
     */
    public static void add(String serviceName, List<ServiceInstance> list) {
        SERVICE_MAP.put(serviceName, list);
    }

    /**
     * remove the expired service
     * @param onlineServices
     */
    public static void removeExpired(List<String> onlineServices) {
        List<String> expiredKeys = Lists.newLinkedList();
        SERVICE_MAP.keySet().forEach(k -> {
            if (!onlineServices.contains(k)) {
                expiredKeys.add(k);
            }
        });
        expiredKeys.forEach(SERVICE_MAP::remove);
    }

}
