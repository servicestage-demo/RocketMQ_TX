package com.huawei.middleware.rocketMqSaga.entity;

import com.huawei.middleware.rocketMqSaga.annotations.RocketMqTxSaga;

import java.lang.annotation.Annotation;

import lombok.Data;

@Data
public class RocketMqTxSagaEntity implements RocketMqTxSaga {

    private String identifier;

    private String txGroup;

    private String topicCommit;

    private String topicRollback;

    private String checkMethod;

    private String cancelMethod;

    public RocketMqTxSagaEntity(String identifier, String txGroup, String topicCommit, String topicRollback,
        String checkMethod, String cancelMethod) {
        this.identifier = identifier;
        this.txGroup = txGroup;
        this.topicCommit = topicCommit;
        this.topicRollback = topicRollback;
        this.checkMethod = checkMethod;
        this.cancelMethod = cancelMethod;
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
        return topicCommit;
    }

    @Override
    public String topicRollback() {
        return topicRollback;
    }

    @Override
    public String checkMethod() {
        return checkMethod;
    }

    @Override
    public String cancelMethod() {
        return cancelMethod;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return RocketMqTxSagaEntity.class;
    }
}
