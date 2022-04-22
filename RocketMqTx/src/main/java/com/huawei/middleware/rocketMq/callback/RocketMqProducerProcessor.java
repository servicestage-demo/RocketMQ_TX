package com.huawei.middleware.rocketMq.callback;

import com.huawei.middleware.rocketMq.annotations.RocketMqTxProducer;
import com.huawei.middleware.rocketMq.entity.RocketMqTxEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;

/*
扫描注解
按照 id : RocketMqTxEntity 格式 存储需要调用方法的bean
 */
public class RocketMqProducerProcessor implements BeanPostProcessor {
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
            if (!method.isAnnotationPresent(RocketMqTxProducer.class)) {
                return;
            }
            RocketMqTxEntity txEntity = new RocketMqTxEntity();
            RocketMqTxProducer rocketMqTxProducer = method.getAnnotation(RocketMqTxProducer.class);
            String identifier = String.format(MQ_IDENTIFIER_FORMAT, rocketMqTxProducer.identifier());
            txEntity.setTxIdentifier(identifier).setCheckBean(bean);
            RocketMqTxRegister.getSingleton().registerTransaction(identifier, txEntity);
        });
        return bean;
    }

    private static class SingletonHolder {
        private static final RocketMqProducerProcessor INSTANCE = new RocketMqProducerProcessor();
    }
}
