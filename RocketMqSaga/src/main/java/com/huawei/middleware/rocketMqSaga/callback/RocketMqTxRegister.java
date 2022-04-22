package com.huawei.middleware.rocketMqSaga.callback;

import com.huawei.middleware.rocketMqSaga.RocketMqConfig;
import com.huawei.middleware.rocketMqSaga.entity.RocketMqTxEntity;

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
