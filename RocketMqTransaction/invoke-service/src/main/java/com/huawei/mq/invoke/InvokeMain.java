package com.huawei.mq.invoke;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableConfigurationProperties
@EnableEurekaClient
@SpringBootApplication(scanBasePackages = {"com.huawei.mq.invoke"})
public class InvokeMain {
    public static void main(String[] args) {
        SpringApplication.run(InvokeMain.class, args);
    }
}
