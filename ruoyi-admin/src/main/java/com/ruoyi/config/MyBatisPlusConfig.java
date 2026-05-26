package com.ruoyi.config;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.baomidou.mybatisplus.annotation.DbType;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
public class MyBatisPlusConfig {

    /**
     * 为你的业务模块创建独立的 SqlSessionFactory
     */
    @Bean(name = "blogSqlSessionFactory")
    public MybatisSqlSessionFactoryBean blogSqlSessionFactory(DataSource dataSource) {
        MybatisSqlSessionFactoryBean sessionFactory = new MybatisSqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        try {
            // 1. 扫描 mapper 下所有子目录的 xml 文件
            sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                    .getResources("classpath*:mapper/**/*.xml"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. 设置 com.ruoyi 下所有包的实体类（会自动扫描所有子包）
        sessionFactory.setTypeAliasesPackage("com.ruoyi");

        // MyBatis-Plus 配置
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setLogImpl(org.apache.ibatis.logging.stdout.StdOutImpl.class);
        sessionFactory.setConfiguration(configuration);

        // 添加分页插件
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        sessionFactory.setPlugins(interceptor);

        return sessionFactory;
    }

    /**
     * 只扫描你业务模块的 Mapper 接口
     */
    @Bean
    public MapperScannerConfigurer blogMapperScanner() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        // 改成扫描 com.ruoyi 下所有子包的 mapper
        mapperScannerConfigurer.setBasePackage("com.ruoyi.**.mapper");
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("blogSqlSessionFactory");
        return mapperScannerConfigurer;
    }
}