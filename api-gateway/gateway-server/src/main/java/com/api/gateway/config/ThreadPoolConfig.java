package com.api.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolTaskScheduler scheduledIndexStatusExecutor() {
        ThreadPoolTaskScheduler syncScheduler = new ThreadPoolTaskScheduler();
        syncScheduler.setPoolSize(5);
        // 这里给线程设置名字，主要是为了在项目能够更快速的定位错误。
        syncScheduler.setThreadGroupName("定时任务线程池");
        syncScheduler.setThreadNamePrefix("threadPoolTaskScheduler-");
        syncScheduler.initialize();
        return syncScheduler;
    }
}
