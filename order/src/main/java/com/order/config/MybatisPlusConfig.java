package com.order.config;

import cn.hutool.core.util.IdUtil;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Slf4j
@Configuration
public class MybatisPlusConfig {

    /**
     * 数据源
     * @return
     */
    @Bean
    @ConfigurationProperties("spring.datasource.druid")
    public DataSource masterDataSource() {
        return DruidDataSourceBuilder.create().build();
    }


    /**
     * MybatisPlus自定义id生成器
     *
     * @return
     */
    @Bean
    public IdentifierGenerator idGenerator() {
        return new CustomIdGenerator();
    }

    /**
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


//    /**
//     * 分页插件
//     */
//    @Bean
//    public PaginationInterceptor paginationInterceptor() {
//        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
//        // paginationInterceptor.setLimit(你的最大单页限制数量，默认 500 条，小于 0 如 -1 不受限制);
//        return paginationInterceptor;
//    }
//
//    /**
//     * 性能分析插件
//     *
//     * @return
//     */
//    @Bean
//    public PerformanceInterceptor performanceInterceptor() {
//        PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
//        performanceInterceptor.setFormat(true);
//        return performanceInterceptor;
//    }
//
//    /**
//     * 乐观锁插件
//     *
//     * @return
//     */
//    @Bean
//    public OptimisticLockerInterceptor optimisticLockerInterceptor() {
//        return new OptimisticLockerInterceptor();
//    }

}
