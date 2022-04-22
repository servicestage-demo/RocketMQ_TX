package com.huawei.middleware.rocketMq.aspect;

import com.huawei.middleware.rocketMq.annotations.RocketMqTxProducer;

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
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class RocketMqTxAspectExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMqTxAspectExecutor.class);

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    private static final String MQ_IDENTIFIER_FORMAT = "rocketmq_identifier_%s";

    @Around(
        "execution(@com.huawei.middleware.rocketMq.annotations.RocketMqTxProducer * *(..)) && @annotation(rocketMqTxProducer)")
    Object intercept(ProceedingJoinPoint joinPoint, RocketMqTxProducer rocketMqTxProducer) throws Throwable {
        try {
            // 封装消息
            Map<String, Object> head = new ConcurrentHashMap<>();

            head.put("txIdentifier", String.format(MQ_IDENTIFIER_FORMAT, rocketMqTxProducer.identifier()));

            Message<String> msg = MessageBuilder.createMessage(JSON.toJSONString(rocketMqTxProducer),
                new MessageHeaders(head));

            //发送事务半消息
            rocketMQTemplate.sendMessageInTransaction(rocketMqTxProducer.topicCommit(), msg, null);

            // 执行事务
            Object res = joinPoint.proceed();
            return res;
        } catch (Throwable throwable) {
            throw throwable;
        } finally {
            LOGGER.info("tx done");
        }
    }
}
