package com.huawei.middleware.rocketMqSaga.callback;

import com.huawei.middleware.rocketMqSaga.annotations.RocketMqTxSaga;
import com.huawei.middleware.rocketMqSaga.entity.RocketMqTxEntity;
import com.huawei.middleware.rocketMqSaga.listener.ConsumerStore;

import com.alibaba.fastjson.JSON;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/*
扫描注解
按照 id : RocketMqTxEntity 格式 存储需要调用方法的bean
 */
public class RocketMqProducerProcessor implements BeanPostProcessor {
    @Value("${rocketmq.name-server}")
    private String NAME_SERVER;

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMqProducerProcessor.class);

    private static final String MQ_IDENTIFIER_FORMAT = "rocketmq_identifier_%s";

    private RocketMqProducerProcessor() {
    }

    public static RocketMqProducerProcessor getSingleInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithMethods(bean.getClass(), (method) -> {
            if (!method.isAnnotationPresent(RocketMqTxSaga.class)) {
                return;
            }
            /**
             * producer 存储  CheckBean + CancelBean + CancelMethod
             */
            RocketMqTxEntity txEntity = new RocketMqTxEntity();
            RocketMqTxSaga rocketMqTxSaga = method.getAnnotation(RocketMqTxSaga.class);
            String identifier = String.format(MQ_IDENTIFIER_FORMAT, rocketMqTxSaga.identifier());

            txEntity.setTxIdentifier(identifier)
                .setCheckBean(bean)
                .setCancelBean(bean)
                .setCancelMethod(rocketMqTxSaga.cancelMethod())
                .setCheckMethod(rocketMqTxSaga.checkMethod())
                .setMethodParam(method.getParameters()[0]);
            RocketMqTxRegister.getSingleton().registerTransaction(identifier, txEntity);
            // producer listener 收到 consumer 失败消息  执行 cancel method
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(rocketMqTxSaga.txGroup());
            consumer.setNamesrvAddr(NAME_SERVER);
            try {
                consumer.subscribe(rocketMqTxSaga.topicRollback(), "*");
                consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
                    try {
                        RocketMqTxEntity Entity = RocketMqTxRegister.getSingleton()
                            .getTxEntity(msgs.get(0).getProperties().get("txIdentifier"));
                        Object param = JSON.parseObject(
                            new String(msgs.get(0).getBody()), Entity.getMethodParam().getParameterizedType());
                        if (Entity.getCancelBean() == null) {
                            LOGGER.error("can not find {} cancel Bean",
                                msgs.get(0).getProperties().get("txIdentifier"));
                            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                        }
                        /**
                         * 确定唯一标识, 已经消费不在重复消费, ROCKETMQ 会保证消息至少会被消费一次. 防止重复消费
                         */
                        if (ConsumerStore.INST.alreadyConsume(msgs.get(0).getMsgId())) {
                            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        }
                        ConsumerStore.INST.add(msgs.get(0).getMsgId());
                        Method cancelMethod = ReflectionUtils.findMethod(txEntity.getCancelBean().getClass(),
                            Entity.getCancelMethod(), param.getClass());
                        if (cancelMethod == null) {
                            LOGGER.error("can not find {} cancelMethod",
                                msgs.get(0).getProperties().get("txIdentifier"));
                            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                        }
                        LOGGER.info("Receive msg from transaction {}, cancelMethod is {}",
                            msgs.get(0).getProperties().get("txIdentifier"),
                            cancelMethod);

                        ReflectionUtils.invokeMethod(cancelMethod, txEntity.getCancelBean(), param);
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    } catch (Exception e) {
                        e.printStackTrace();
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
        private static final RocketMqProducerProcessor INSTANCE = new RocketMqProducerProcessor();
    }
}
