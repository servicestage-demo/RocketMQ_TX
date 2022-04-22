package com.huawei.middleware.rocketMq;

import com.huawei.middleware.rocketMq.callback.RocketMqConsumerProcessor;
import com.huawei.middleware.rocketMq.callback.RocketMqProducerProcessor;
import com.huawei.middleware.rocketMq.entity.RocketMqTxEntity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;

@Getter
@Configuration
public class RocketMqConfig {

    private static final Map<String, RocketMqTxEntity> rocketMqBeanMap = new ConcurrentHashMap<>();

    public static Map<String, RocketMqTxEntity> getEntities() {
        return rocketMqBeanMap;
    }

    @Bean
    public RocketMqProducerProcessor rocketMqProducerProcessor() {
        return RocketMqProducerProcessor.getSingleInstance();
    }

    @Bean
    public RocketMqConsumerProcessor rocketMqConsumerProcessor() {
        return RocketMqConsumerProcessor.getSingleInstance();
    }


}
