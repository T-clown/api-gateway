package com.api.gateway.config;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {
    @Bean
    public IdentifierGenerator idGenerator() {
        return new CustomIdGenerator();
    }

    /**
     * MybatisPlus自定义id生成器
     * 也可以直接在类上加上注解@Component，两种方式都是交给注入到Spring中
     */
    static class CustomIdGenerator implements IdentifierGenerator {

        @Override
        public boolean assignId(Object idValue) {
            return IdentifierGenerator.super.assignId(idValue);
        }

        @Override
        public Number nextId(Object entity) {
            return IdUtil.getSnowflakeNextId();
        }

        @Override
        public String nextUUID(Object entity) {
            return IdentifierGenerator.super.nextUUID(entity);
        }
    }

}
