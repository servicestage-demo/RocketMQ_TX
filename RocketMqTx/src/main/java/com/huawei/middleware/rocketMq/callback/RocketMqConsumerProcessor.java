package com.huawei.middleware.rocketMq.callback;

import com.huawei.middleware.rocketMq.annotations.RocketMqTxConsumer;
import com.huawei.middleware.rocketMq.listener.ConsumerStore;
import com.huawei.middleware.rocketMq.entity.RocketMqTxEntity;

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
public class RocketMqConsumerProcessor implements BeanPostProcessor {
    @Value("${rocketmq.name-server}")
    private String NAME_SERVER;

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
            RocketMqTxEntity txEntity = new RocketMqTxEntity();
            RocketMqTxConsumer rocketMqTxConsumer = method.getAnnotation(RocketMqTxConsumer.class);
            String identifier = String.format(MQ_IDENTIFIER_FORMAT, rocketMqTxConsumer.identifier());
            txEntity.setTxIdentifier(identifier).setExecuteBean(bean).setExecuteMethod(method.getName());
            RocketMqTxRegister.getSingleton().registerTransaction(identifier, txEntity);

            // 消费者 listener 收到消息 回调 业务方法
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(rocketMqTxConsumer.txGroup());
            consumer.setNamesrvAddr(NAME_SERVER);
            try {
                consumer.subscribe(rocketMqTxConsumer.topicCommit(), "*");
                consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
                    try {
                        RocketMqTxEntity Entity = RocketMqTxRegister.getSingleton()
                            .getTxEntity(msgs.get(0).getProperties().get("txIdentifier"));

                        if (Entity.getExecuteBean() == null) {
                            LOGGER.error("can not find {} execute Bean",
                                msgs.get(0).getProperties().get("txIdentifier"));
                            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                        }
                        /**
                         * 确定唯一标识, 已经消费不在重复消费, ROCKETMQ 会保证消息至少会被消费一次. 防止重复消费
                         */
                        if (ConsumerStore.INST.alreadyConsume(msgs.get(0).getProperties().get("txIdentifier"))) {
                            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        }
                        ConsumerStore.INST.add(msgs.get(0).getProperties().get("txIdentifier"));
                        Method executeMethod = ReflectionUtils.findMethod(txEntity.getExecuteBean().getClass(),
                            txEntity.getExecuteMethod());
                        if (executeMethod == null) {
                            LOGGER.error("can not find {} executeMethod",
                                msgs.get(0).getProperties().get("txIdentifier"));
                            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                        }
                        LOGGER.info("Receive msg from transaction {}, executeMethod is {}",
                            msgs.get(0).getProperties().get("txIdentifier"),
                            executeMethod);

                        ReflectionUtils.invokeMethod(executeMethod, txEntity.getExecuteBean());
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
        private static final RocketMqConsumerProcessor INSTANCE = new RocketMqConsumerProcessor();
    }
}
