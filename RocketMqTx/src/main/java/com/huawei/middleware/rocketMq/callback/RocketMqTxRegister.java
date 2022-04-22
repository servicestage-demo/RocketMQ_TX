package com.huawei.middleware.rocketMq.callback;

import com.huawei.middleware.rocketMq.RocketMqConfig;
import com.huawei.middleware.rocketMq.entity.RocketMqTxEntity;

import java.util.Map;

public class RocketMqTxRegister {

    private final Map<String, RocketMqTxEntity> rocketMqBeanMap;

    private RocketMqTxRegister() {
        rocketMqBeanMap = RocketMqConfig.getEntities();
    }

    public static RocketMqTxRegister getSingleton() {
        return RocketMqTransactionalRegisterSingletonFactory.INSTANCE;
    }

    public void registerTransaction(String identifier, RocketMqTxEntity txEntity) {
        if (rocketMqBeanMap.containsKey(identifier)) {
            rocketMqBeanMap.put(identifier, rocketMqBeanMap.get(identifier).addEntity(txEntity));
        } else {
            rocketMqBeanMap.put(identifier, txEntity);
        }
    }

    public RocketMqTxEntity getTxEntity(String identifier) {
        return rocketMqBeanMap.get(identifier);
    }

    private static class RocketMqTransactionalRegisterSingletonFactory {
        private static final RocketMqTxRegister INSTANCE = new RocketMqTxRegister();
    }
}
