package com.huawei.middleware.rocketMqSaga.aspect;

import com.huawei.middleware.rocketMqSaga.TxResultCache;
import com.huawei.middleware.rocketMqSaga.annotations.RocketMqTxSaga;

import com.alibaba.fastjson.JSON;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class RocketMqTxAspectExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMqTxAspectExecutor.class);

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    private static final String MQ_IDENTIFIER_FORMAT = "rocketmq_identifier_%s_%s";

    private static final String MQ_IDENTIFIER = "rocketmq_identifier_%s";

    @Around(
        "execution(@com.huawei.middleware.rocketMqSaga.annotations.RocketMqTxSaga * *(..)) && @annotation(rocketMqTxSaga)")
    Object intercept(ProceedingJoinPoint joinPoint, RocketMqTxSaga rocketMqTxSaga) throws Throwable {
        String txid = String.format(MQ_IDENTIFIER_FORMAT, rocketMqTxSaga.identifier(), UUID.randomUUID());
        try {
            // 封装消息
            Map<String, Object> head = new ConcurrentHashMap<>();
            // todo : only use txId
            head.put("txIdentifier", String.format(MQ_IDENTIFIER, rocketMqTxSaga.identifier()));
            head.put("txId", txid);

            Message<String> msg = MessageBuilder.createMessage(JSON.toJSONString(joinPoint.getArgs()[0]),
                new MessageHeaders(head));

            // 执行事务
            Object res = joinPoint.proceed();
            TxResultCache.store(txid, true);

            //发送事务半消息
            rocketMQTemplate.sendMessageInTransaction(rocketMqTxSaga.topicCommit(), msg, null);
            return res;
        } catch (Throwable throwable) {
            TxResultCache.store(txid, false);
            throw throwable;
        }
    }
}
