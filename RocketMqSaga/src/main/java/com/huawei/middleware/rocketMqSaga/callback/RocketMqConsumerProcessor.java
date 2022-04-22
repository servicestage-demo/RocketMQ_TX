package com.huawei.middleware.rocketMqSaga.callback;

import com.huawei.middleware.rocketMqSaga.annotations.RocketMqTxConsumer;
import com.huawei.middleware.rocketMqSaga.exception.RocketMqSagaException;
import com.huawei.middleware.rocketMqSaga.listener.ConsumerStore;
import com.huawei.middleware.rocketMqSaga.entity.RocketMqTxEntity;

import com.alibaba.fastjson.JSON;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RocketMqConsumerProcessor implements BeanPostProcessor {
    @Value("${rocketmq.name-server}")
    private String NAME_SERVER;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMqConsumerProcessor.class);

    private static final String MQ_IDENTIFIER_FORMAT = "rocketmq_identifier_%s";

    private RocketMqConsumerProcessor() {
    }

    public static RocketMqConsumerProcessor getSingleInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithMethods(bean.getClass(), (method) -> {
            if (!method.isAnnotationPresent(RocketMqTxConsumer.class)) {
                return;
            }
            /**
             * consumer 存储  ExecuteBean + ExecuteMethod
             */
            RocketMqTxEntity txEntity = new RocketMqTxEntity();
            RocketMqTxConsumer rocketMqTxConsumer = method.getAnnotation(RocketMqTxConsumer.class);
            String identifier = String.format(MQ_IDENTIFIER_FORMAT, rocketMqTxConsumer.identifier());
            method.getParameters();
            txEntity.setTxIdentifier(identifier)
                .setExecuteBean(bean)
                .setExecuteMethod(method.getName())
                .setMethodParam(method.getParameters()[0]);
            RocketMqTxExecuteRegister.getSingleton().registerTransaction(identifier, txEntity);
            // 消费者 listener 收到消息 回调 业务方法
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(rocketMqTxConsumer.txGroup());
            consumer.setNamesrvAddr(NAME_SERVER);
            try {
                consumer.subscribe(rocketMqTxConsumer.topicCommit(), "*");
                consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
                    try {
                        String txIdentifier = msgs.get(0).getProperties().get("txIdentifier");
                        RocketMqTxEntity consumerEntity = RocketMqTxExecuteRegister.getSingleton()
                            .getTxEntity(txIdentifier);
                        Object param = JSON.parseObject(
                            new String(msgs.get(0).getBody()), consumerEntity.getMethodParam().getParameterizedType());
                        if (consumerEntity.getExecuteBean() == null) {
                            LOGGER.error("can not find {} execute Bean", txIdentifier);
                            throw new RocketMqSagaException("can not find " + txIdentifier + "execute Bean");
                        }
                        /**
                         * 确定唯一标识, 已经消费不在重复消费, ROCKETMQ 会保证消息至少会被消费一次. 防止重复消费
                         */
                        if (ConsumerStore.INST.alreadyConsume(msgs.get(0).getMsgId())) {
                            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        }
                        ConsumerStore.INST.add(msgs.get(0).getMsgId());
                        Method executeMethod = ReflectionUtils.findMethod(consumerEntity.getExecuteBean().getClass(),
                            consumerEntity.getExecuteMethod(), param.getClass());

                        if (executeMethod == null) {
                            LOGGER.error("can not find {} executeMethod", txIdentifier);
                            throw new RocketMqSagaException("can not find " + txIdentifier + "execute Method");
                        }
                        LOGGER.info("Receive msg from transaction {}, executeMethod is {}", txIdentifier,
                            executeMethod);

                        ReflectionUtils.invokeMethod(executeMethod, consumerEntity.getExecuteBean(), param);
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    } catch (Exception e) {
                        /*
                            消费者 本地事务执行失败  发送消息
                            消息中需要包含  txIdentifier body 中有 param
                         */
                        LOGGER.error("localTx fail, send message to invoke cancelMethod : {}", e.getMessage());
                        Map<String, Object> head = new ConcurrentHashMap<>();
                        head.put("txIdentifier", msgs.get(0).getProperties().get("txIdentifier"));
                        rocketMQTemplate.send(rocketMqTxConsumer.topicRollback(),
                            MessageBuilder.createMessage(msgs.get(0).getBody(),
                                new MessageHeaders(head)));
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                });
                consumer.start();
            } catch (MQClientException e) {
                e.printStackTrace();
            }
        });
        return bean;
    }

    private static class SingletonHolder {
        private static final RocketMqConsumerProcessor INSTANCE = new RocketMqConsumerProcessor();
    }
}
