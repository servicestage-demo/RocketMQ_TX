package com.huawei.mq.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@EnableConfigurationProperties
@SpringBootApplication(scanBasePackages = {"com.huawei.mq.inventory", "com.huawei.middleware.rocketMqSaga"})
public class InventoryMain {
    public static void main(String[] args) {
        SpringApplication.run(InventoryMain.class, args);
    }
}
