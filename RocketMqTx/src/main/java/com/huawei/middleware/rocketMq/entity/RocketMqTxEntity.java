package com.huawei.middleware.rocketMq.entity;

import lombok.Getter;

@Getter
public class RocketMqTxEntity {

    private String txIdentifier;

    private Object cancelBean;

    private Object checkBean;

    private Object executeBean;

    private String executeMethod;

    public RocketMqTxEntity() {
    }

    public RocketMqTxEntity(String txIdentifier, Object cancelBean, Object checkBean, Object executeBean) {
        this.txIdentifier = txIdentifier;
        this.cancelBean = cancelBean;
        this.checkBean = checkBean;
        this.executeBean = executeBean;
    }

    public RocketMqTxEntity setTxIdentifier(String txIdentifier) {
        this.txIdentifier = txIdentifier;
        return this;
    }

    public RocketMqTxEntity setCancelBean(Object cancelBean) {
        this.cancelBean = cancelBean;
        return this;
    }

    public RocketMqTxEntity setCheckBean(Object checkBean) {
        this.checkBean = checkBean;
        return this;
    }

    public RocketMqTxEntity setExecuteBean(Object executeBean) {
        this.executeBean = executeBean;
        return this;
    }

    public RocketMqTxEntity setExecuteMethod(String executeMethod) {
        this.executeMethod = executeMethod;
        return this;
    }

    public RocketMqTxEntity addEntity(RocketMqTxEntity entity) {
        if (this.cancelBean == null) {
            this.cancelBean = entity.cancelBean;
        }
        if (this.executeBean == null) {
            this.executeBean = entity.executeBean;
        }
        if (this.checkBean == null) {
            this.checkBean = entity.checkBean;
        }
        if (this.executeMethod == null) {
            this.executeMethod = entity.executeMethod;
        }
        return this;
    }
}
