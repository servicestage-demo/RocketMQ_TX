package com.huawei.mq.inventory.config;

import com.huawei.mq.inventory.service.InventoryService;

import com.alibaba.druid.pool.DruidDataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * inventory 增加数据源
 */
@Configuration
public class WebConfig {
    @Value("${spring.datasource.bank.url}")
    private String dbUrl;

    @Value("${spring.datasource.bank.username}")
    private String username;

    @Value("${spring.datasource.bank.password}")
    private String password;

    @Value("${spring.datasource.bank.driver-class-name}")
    private String driverClassName;

    @Bean(name = "inventoryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.bank")
    public DataSource bankDataSource() {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(dbUrl);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setDriverClassName(driverClassName);
        datasource.setInitialSize(10);
        datasource.setMaxActive(100);
        return datasource;
    }

    @Bean(name = "inventoryJdbcTemplate")
    public JdbcTemplate bankJdbcTemplate(@Qualifier("inventoryDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public InventoryService inventoryService(@Qualifier("inventoryJdbcTemplate") JdbcTemplate JdbcTemplate,
        @Qualifier("inventoryDataSource") DataSource dataSource) {
        return new InventoryService(JdbcTemplate, dataSource);
    }
}
