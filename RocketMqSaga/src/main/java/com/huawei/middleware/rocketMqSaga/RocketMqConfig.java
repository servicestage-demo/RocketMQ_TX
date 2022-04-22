package com.huawei.middleware.rocketMqSaga;

import com.huawei.middleware.rocketMqSaga.callback.RocketMqConsumerProcessor;
import com.huawei.middleware.rocketMqSaga.callback.RocketMqProducerProcessor;
import com.huawei.middleware.rocketMqSaga.entity.RocketMqTxEntity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;

@Getter
@Configuration
public class RocketMqConfig {

    private static final Map<String, RocketMqTxEntity> rocketMqBeanMap = new ConcurrentHashMap<>();

    private static final Map<String, RocketMqTxEntity> rocketMqExecuteBeanMap = new ConcurrentHashMap<>();

    public static Map<String, RocketMqTxEntity> getEntities() {
        return rocketMqBeanMap;
    }

    public static Map<String, RocketMqTxEntity> getExecuteEntities() {
        return rocketMqExecuteBeanMap;
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
