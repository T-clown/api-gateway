package com.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Types;
import java.util.Collections;

public class MybatisPlusGenerator {

    public static void main(String[] args) {
        generator();
    }

    private static void generator() {
        String path = System.getProperty("user.dir");
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/springboot?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf8", "root", "root123456")
                //全局配置
                .globalConfig(builder -> builder
                        // 设置作者
                        .author("ti")
                        // 开启 swagger 模式
                        //.enableSwagger()
                        // 指定输出目录
                        .outputDir(path + "/src/main/java")
                        .dateType(DateType.TIME_PACK)
                        .commentDate("yyyy-MM-dd")
                        .build()
                )
                .dataSourceConfig(builder -> builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                    int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                    if (typeCode == Types.SMALLINT) {
                        // 自定义类型转换
                        return DbColumnType.INTEGER;
                    }
                    return typeRegistry.getColumnType(metaInfo);
                }))
                //包配置
                .packageConfig(builder -> builder
                        // 设置父包名
                        .parent("com.order")
                        // 设置父包模块名
                        // .moduleName("springboot")
                        // 设置mapperXml生成路径
                        .pathInfo(Collections.singletonMap(OutputFile.xml, path + "/src/main/resources/mapper"))
                        .entity("entity")
                        .service("service")
                        .serviceImpl("service.impl")
                        .mapper("mapper")
                        //.xml("mapper.xml")
                        .controller("controller")
                        .build()
                )
                //Entity策略配置
                .strategyConfig(builder -> builder.entityBuilder()
                        //.superClass(BaseEntity.class)
                        .disableSerialVersionUID()
                        .enableFileOverride()
                        .enableChainModel()
                        .enableColumnConstant()
                        .enableLombok()
                        .enableRemoveIsPrefix()
                        .enableTableFieldAnnotation()
                        .enableActiveRecord()
                        .versionColumnName("version")
                        .versionPropertyName("version")
                        //逻辑删除字段名(数据库)
                        .logicDeleteColumnName("deleted")
                        //逻辑删除属性名(实体)
                        .logicDeletePropertyName("deleted")
                        //数据库表映射到实体的命名策略
                        .naming(NamingStrategy.underline_to_camel)
                        //数据库表字段映射到实体的命名策略
                        .columnNaming(NamingStrategy.underline_to_camel)
                        //.addSuperEntityColumns("id", "created_by", "created_time", "updated_by", "updated_time")
                        //.addIgnoreColumns("age")
                        //.addTableFills(new Column("create_time", FieldFill.INSERT))
                        //.addTableFills(new Property("updateTime", FieldFill.INSERT_UPDATE))
                        .idType(IdType.ASSIGN_ID)
                        .formatFileName("%sEntity")
                )
                //Controller策略配置
                .strategyConfig(builder -> builder.controllerBuilder()
                        //.enableFileOverride()
                        // .superClass(BaseController.class)
                        .enableHyphenStyle()
                        .enableRestStyle()
                        .formatFileName("%sController")
                )
                //Service策略配置
                .strategyConfig(builder -> builder.serviceBuilder()
                        //.enableFileOverride()
                        // .superServiceClass(BaseService.class)
                        //.superServiceImplClass(BaseServiceImpl.class)
                        .formatServiceFileName("%sService")
                        .formatServiceImplFileName("%sServiceImpl")
                )
                //Mapper 策略配置
                .strategyConfig(builder -> builder.mapperBuilder()
                        .enableFileOverride()
                        .mapperBuilder()
                        .superClass(BaseMapper.class)
                        .enableFileOverride()
                        .mapperAnnotation(Mapper.class)
                        .enableBaseResultMap()
                        .enableBaseColumnList()
                        //.cache(MyMapperCache.class)
                        .formatMapperFileName("%sMapper")
                        .formatXmlFileName("%sMapper"))
                //策略配置
                .strategyConfig(builder -> builder
                        // 设置需要生成的表名
                        .addInclude("springboot_order")
                        // 设置过滤表前缀
                        .addTablePrefix("springboot")

                )
                //注入配置
                .injectionConfig(builder -> builder
                                .beforeOutputFile((tableInfo, objectMap) -> {
                                    System.out.println("tableInfo: " + tableInfo.getEntityName() + " objectMap: " + objectMap.size());
                                })
                                .customMap(Collections.singletonMap("test", "baomidou"))
                        //自定义配置模板文件
                        //.customFile(Collections.singletonMap("test.txt", "/templates/test.vm"))
                )
                // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                //.templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }

}
