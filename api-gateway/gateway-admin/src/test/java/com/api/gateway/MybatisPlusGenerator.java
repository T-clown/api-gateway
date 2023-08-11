//package com.api.gateway;
//
//import cn.hutool.core.io.resource.ResourceUtil;
//import cn.hutool.core.lang.Dict;
//import cn.hutool.setting.yaml.YamlUtil;
//import com.api.gateway.config.MybatisPlusConfig;
//import com.baomidou.mybatisplus.annotation.FieldFill;
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.core.mapper.BaseMapper;
//import com.baomidou.mybatisplus.generator.FastAutoGenerator;
//import com.baomidou.mybatisplus.generator.config.rules.DateType;
//import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
//import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
//import com.baomidou.mybatisplus.generator.fill.Column;
//import com.baomidou.mybatisplus.generator.fill.Property;
//import org.apache.ibatis.annotations.Mapper;
//import org.springframework.util.ResourceUtils;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.sql.Types;
//import java.util.Collections;
//import java.util.HashMap;
//
//public class MybatisPlusGenerator {
//
//    public static void main(String[] args) {
//        generator();
//    }
//
//    private static void generator(){
//        FastAutoGenerator.create("jdbc:mysql://localhost:3306/master?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf8", "root", "root123456")
//                //全局配置
//                .globalConfig(builder -> builder
//                        // 设置作者
//                        .author("ti")
//                        // 开启 swagger 模式
//                        .enableSwagger()
//                        // 指定输出目录
//                        .outputDir("/Users/hrtps/workspace/springboot/springboot-api-gateway" + "/src/main/java")
//                        .dateType(DateType.TIME_PACK)
//                        .commentDate("yyyy-MM-dd")
//                        .build()
//                )
//                .dataSourceConfig(builder -> builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
//                    int typeCode = metaInfo.getJdbcType().TYPE_CODE;
//                    if (typeCode == Types.SMALLINT) {
//                        // 自定义类型转换
//                        return DbColumnType.INTEGER;
//                    }
//                    return typeRegistry.getColumnType(metaInfo);
//                }))
//                //包配置
//                .packageConfig(builder -> builder
//                        // 设置父包名
//                        .parent("com.springboot.auth")
//                        // 设置父包模块名
//                        // .moduleName("springboot")
//                        // 设置mapperXml生成路径
//                        //.pathInfo(Collections.singletonMap(OutputFile.xml, "/opt/baomidou"))
//                        .entity("entity")
//                        .service("service")
//                        .serviceImpl("service.impl")
//                        .mapper("mapper")
//                        .xml("mapper.xml")
//                        .controller("controller")
//                        .build()
//                )
//                //Entity策略配置
//                .strategyConfig(builder -> builder.entityBuilder()
//                        //.superClass(BaseEntity.class)
//                        .disableSerialVersionUID()
//                        .enableFileOverride()
//                        .enableChainModel()
//                        .enableColumnConstant()
//                        .enableLombok()
//                        .enableRemoveIsPrefix()
//                        .enableTableFieldAnnotation()
//                        .enableActiveRecord()
//                        .versionColumnName("version")
//                        .versionPropertyName("version")
//                        //逻辑删除字段名(数据库)
//                        .logicDeleteColumnName("deleted")
//                        //逻辑删除属性名(实体)
//                        .logicDeletePropertyName("deleted")
//                        //数据库表映射到实体的命名策略
//                        .naming(NamingStrategy.underline_to_camel)
//                        //数据库表字段映射到实体的命名策略
//                        .columnNaming(NamingStrategy.underline_to_camel)
//                        .addSuperEntityColumns("id", "created_by", "created_time", "updated_by", "updated_time")
//                        .addIgnoreColumns("age")
//                        .addTableFills(new Column("create_time", FieldFill.INSERT))
//                        .addTableFills(new Property("updateTime", FieldFill.INSERT_UPDATE))
//                        .idType(IdType.AUTO)
//                        .formatFileName("%sEntity")
//                )
//                //Controller策略配置
//                .strategyConfig(builder -> builder.controllerBuilder()
//                        .enableFileOverride()
//                        // .superClass(BaseController.class)
//                        .enableHyphenStyle()
//                        .enableRestStyle()
//                        .formatFileName("%sController")
//                )
//                //Service策略配置
//                .strategyConfig(builder -> builder.serviceBuilder()
//                        .serviceBuilder()
//                        // .superServiceClass(BaseService.class)
//                        //.superServiceImplClass(BaseServiceImpl.class)
//                        .formatServiceFileName("%sService")
//                        .formatServiceImplFileName("%sServiceImpl")
//                )
//                //Mapper 策略配置
//                .strategyConfig(builder -> builder.mapperBuilder()
//                        .mapperBuilder()
//                        .superClass(BaseMapper.class)
//                        .enableFileOverride()
//                        .mapperAnnotation(Mapper.class)
//                        .enableBaseResultMap()
//                        .enableBaseColumnList()
//                        //.cache(MyMapperCache.class)
//                        .formatMapperFileName("%sDao")
//                        .formatXmlFileName("%sXml"))
//                //策略配置
//                .strategyConfig(builder -> builder
//                        // 设置需要生成的表名
//                        .addInclude("springboot_user")
//                        // 设置过滤表前缀
//                        .addTablePrefix("springboot")
//
//                )
//                //注入配置
//                .injectionConfig(builder -> builder
//                                .beforeOutputFile((tableInfo, objectMap) -> {
//                                    System.out.println("tableInfo: " + tableInfo.getEntityName() + " objectMap: " + objectMap.size());
//                                })
//                                .customMap(Collections.singletonMap("test", "baomidou"))
//                        //自定义配置模板文件
//                        //.customFile(Collections.singletonMap("test.txt", "/templates/test.vm"))
//                )
//                // 使用Freemarker引擎模板，默认的是Velocity引擎模板
//                //.templateEngine(new FreemarkerTemplateEngine())
//                .execute();
//    }
//
//
//
//    /**
//     * 1.尽量不要使用相对于System.getProperty（"user.dir"）当前用户目录的相对路径。这是一颗定时炸弹，随时可能要你的命。
//     * 2.尽量使用URI形式的绝对路径资源。它可以很容易的转变为URI，URL，File对象。
//     * 3.尽量使用相对classpath的相对路径。不要使用绝对路径。
//     * 使用上面ClassLoaderUtil类的public static URL getExtendResource（String relativePath）方法已经能够使用相对于classpath的相对路径定位所有位置的资源。
//     * 4.绝对不要使用硬编码的绝对路径。因为，我们完全可以使用ClassLoader类的getResource（""）方法得到当前classpath的绝对路径。
//     * 如果你一定要指定一个绝对路径，那么使用配置文件，也比硬编码要好得多
//     *
//     */
//    private static void path() {
//        System.out.println(System.getProperty("user.dir"));
//        //得到的是当前类class文件的URI目录。不包括自己
//        System.out.println(MybatisPlusConfig.class.getResource(""));
//        //得到的是当前的classpath的绝对URI路径
//        System.out.println(MybatisPlusConfig.class.getResource("/"));
//        //得到的也是当前ClassPath的绝对URI路径
//        System.out.println(MybatisPlusConfig.class.getClassLoader().getResource(""));
//        //得到的也是当前ClassPath的绝对URI路径
//        System.out.println(ClassLoader.getSystemResource(""));
//        //得到的也是当前ClassPath的绝对URI路径
//        System.out.println(Thread.currentThread().getContextClassLoader ().getResource(""));
//    }
//
//    private static void value() throws FileNotFoundException {
//        File file = ResourceUtils.getFile("classpath:application.yml");
//        String file1 = ResourceUtil.getResource("classpath:application.yml").getFile();
//        Dict dict = YamlUtil.loadByPath("classpath:application.yml");
//        Object byPath = dict.getByPath("spring.datasource.username");
//        HashMap hashMap = YamlUtil.loadByPath("classpath:application.yml", HashMap.class);
//        Object o = dict.get("spring.datasource.username");
//        System.out.println(o.toString());
//    }
//}
