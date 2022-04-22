package com.huawei.middleware.rocketMq.listener;

import com.huawei.middleware.rocketMq.callback.RocketMqTxRegister;
import com.huawei.middleware.rocketMq.entity.RocketMqTxEntity;
import com.huawei.middleware.rocketMq.entity.RocketMqTxProducerEntity;

import com.alibaba.fastjson.JSON;

import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

@RocketMQTransactionListener
public class RocketMqCheckListener implements RocketMQLocalTransactionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMqCheckListener.class);

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message,
        Object arg) {
        LOGGER.info("start check local tx state");
        return RocketMQLocalTransactionState.UNKNOWN;
    }

    /*
        调用客户提供check方法 判断事务执行结果
        executeLocalTransaction 返回UNKNOWN MQ每隔一段时间调用一次checkLocalTransaction
        BrokerConfig.class 中配置 默认 transactionCheckInterval=60000 transactionCheckMax=15
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {

        RocketMqTxProducerEntity rocketMqTransactionalEntity = JSON.parseObject(
            new String((byte[]) message.getPayload()), RocketMqTxProducerEntity.class);

        RocketMqTxEntity txEntity = RocketMqTxRegister.getSingleton()
            .getTxEntity((String) message.getHeaders().get("txIdentifier"));
        if (txEntity.getCheckBean() == null) {
            LOGGER.error("can not find {} checkMethod Bean", rocketMqTransactionalEntity.identifier());
            return RocketMQLocalTransactionState.ROLLBACK;
        }

        Method checkMethod = ReflectionUtils.findMethod(txEntity.getCheckBean().getClass(),
            rocketMqTransactionalEntity.getCheckMethod());

        if (checkMethod == null) {
            LOGGER.error("can not find {} checkMethod", rocketMqTransactionalEntity.identifier());
            return RocketMQLocalTransactionState.ROLLBACK;
        }

        LOGGER.info("Receive msg from transaction {}, checkMethod is {}", rocketMqTransactionalEntity.identifier(),
            checkMethod);

        if ((boolean) ReflectionUtils.invokeMethod(checkMethod, txEntity.getCheckBean())) {
            return RocketMQLocalTransactionState.COMMIT;
        } else {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }
}
