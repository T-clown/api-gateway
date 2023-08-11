package com.api.gateway.utils;

import org.springframework.util.Assert;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 */
public class IpUtil {

    /**
     * 获取ip地址
     *
     * @return
     */
    public static String getLocalIpAddress() {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Assert.notNull(address, "获取机器ip地址失败");
        return address.getHostAddress();
    }
}
