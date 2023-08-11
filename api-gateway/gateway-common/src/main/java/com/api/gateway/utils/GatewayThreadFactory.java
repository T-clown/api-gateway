package com.api.gateway.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ThreadFactory;


public class GatewayThreadFactory {

    private String name;

    private Boolean daemon;

    public GatewayThreadFactory(String name, Boolean daemon) {
        this.name = name;
        this.daemon = daemon;
    }

    public ThreadFactory create() {
        return new ThreadFactoryBuilder().setNameFormat(this.name + "-%d").setDaemon(this.daemon).build();
    }

}
