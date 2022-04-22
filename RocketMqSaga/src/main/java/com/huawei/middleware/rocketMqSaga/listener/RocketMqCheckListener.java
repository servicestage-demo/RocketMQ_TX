package com.huawei.middleware.rocketMqSaga.listener;

import com.huawei.middleware.rocketMqSaga.TxResultCache;
import com.huawei.middleware.rocketMqSaga.callback.RocketMqTxRegister;
import com.huawei.middleware.rocketMqSaga.entity.RocketMqTxEntity;

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
        boolean res = TxResultCache.query((String) message.getHeaders().get("txId"));
        if (res) {
            return RocketMQLocalTransactionState.COMMIT;
        }
        return RocketMQLocalTransactionState.UNKNOWN;
    }

    /*
        调用客户提供check方法 判断事务执行结果
        executeLocalTransaction 返回UNKNOWN MQ每隔一段时间调用一次checkLocalTransaction
        BrokerConfig.class 中配置 默认 transactionCheckInterval=60000 transactionCheckMax=15
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {

        RocketMqTxEntity txEntity = RocketMqTxRegister.getSingleton()
            .getTxEntity((String) message.getHeaders().get("txIdentifier"));

        Object param = JSON.parseObject(
            new String((byte[]) message.getPayload()), txEntity.getMethodParam().getParameterizedType());

        if (txEntity.getCheckBean() == null) {
            LOGGER.error("can not find {} checkMethod Bean", txEntity.getTxIdentifier());
            return RocketMQLocalTransactionState.ROLLBACK;
        }

        Method checkMethod = ReflectionUtils.findMethod(txEntity.getCheckBean().getClass(),
            txEntity.getCheckMethod(), param.getClass());

        if (checkMethod == null) {
            LOGGER.error("can not find {} checkMethod", txEntity.getTxIdentifier());
            return RocketMQLocalTransactionState.ROLLBACK;
        }

        LOGGER.info("Receive msg from transaction {}, checkMethod is {}", txEntity.getTxIdentifier(),
            checkMethod);

        if ((boolean) ReflectionUtils.invokeMethod(checkMethod, txEntity.getCheckBean(), param)) {
            return RocketMQLocalTransactionState.COMMIT;
        } else {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }
}
