package com.huawei.middleware.rocketMqSaga.callback;

import com.huawei.middleware.rocketMqSaga.RocketMqConfig;
import com.huawei.middleware.rocketMqSaga.entity.RocketMqTxEntity;

import java.util.Map;

public class RocketMqTxExecuteRegister {

    private final Map<String, RocketMqTxEntity> rocketExecuteMqBeanMap;

    private RocketMqTxExecuteRegister() {
        rocketExecuteMqBeanMap = RocketMqConfig.getExecuteEntities();
    }

    public static RocketMqTxExecuteRegister getSingleton() {
        return RocketMqTransactionalRegisterSingletonFactory.INSTANCE;
    }

    public void registerTransaction(String identifier, RocketMqTxEntity txEntity) {
        if (!rocketExecuteMqBeanMap.containsKey(identifier)) {
            rocketExecuteMqBeanMap.put(identifier, txEntity);
        }
    }

    public RocketMqTxEntity getTxEntity(String identifier) {
        return rocketExecuteMqBeanMap.get(identifier);
    }

    private static class RocketMqTransactionalRegisterSingletonFactory {
        private static final RocketMqTxExecuteRegister INSTANCE = new RocketMqTxExecuteRegister();
    }
}
