package com.huawei.middleware.rocketMq.entity;

import com.huawei.middleware.rocketMq.annotations.RocketMqTxProducer;

import java.lang.annotation.Annotation;

import lombok.Data;

@Data
public class RocketMqTxProducerEntity implements RocketMqTxProducer {

    private String identifier;

    private String txGroup;

    private String topic;

    private String checkMethod;

    public RocketMqTxProducerEntity(String identifier, String txGroup, String topic, String checkMethod) {
        this.identifier = identifier;
        this.txGroup = txGroup;
        this.topic = topic;
        this.checkMethod = checkMethod;
    }

    @Override
    public String identifier() {
        return identifier;
    }

    @Override
    public String txGroup() {
        return txGroup;
    }

    @Override
    public String topicCommit() {
        return topic;
    }

    @Override
    public String checkMethod() {
        return checkMethod;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return RocketMqTxProducerEntity.class;
    }
}
