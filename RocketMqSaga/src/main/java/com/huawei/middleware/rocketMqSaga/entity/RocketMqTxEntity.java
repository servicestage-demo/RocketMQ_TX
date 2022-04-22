package com.huawei.middleware.rocketMqSaga.entity;

import java.lang.reflect.Parameter;

import lombok.Getter;

@Getter
public class RocketMqTxEntity {

    private String txIdentifier;

    private Parameter methodParam;

    private Object cancelBean;

    private String cancelMethod;

    private Object checkBean;

    private String checkMethod;

    private Object executeBean;

    private String executeMethod;

    public RocketMqTxEntity() {
    }

    public RocketMqTxEntity setMethodParam(Parameter methodParam) {
        this.methodParam = methodParam;
        return this;
    }

    public RocketMqTxEntity setTxIdentifier(String txIdentifier) {
        this.txIdentifier = txIdentifier;
        return this;
    }

    public RocketMqTxEntity setCancelBean(Object cancelBean) {
        this.cancelBean = cancelBean;
        return this;
    }

    public RocketMqTxEntity setCancelMethod(String cancelMethod) {
        this.cancelMethod = cancelMethod;
        return this;
    }

    public RocketMqTxEntity setCheckMethod(String checkMethod) {
        this.checkMethod = checkMethod;
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
        if (this.methodParam == null) {
            this.methodParam = entity.methodParam;
        }
        if (this.cancelMethod == null) {
            this.cancelMethod = entity.cancelMethod;
        }
        if (this.cancelBean == null) {
            this.cancelBean = entity.cancelBean;
        }
        if (this.checkBean == null) {
            this.checkBean = entity.checkBean;
        }
        if (this.checkMethod == null) {
            this.checkMethod = entity.checkMethod;
        }
        if (this.executeBean == null) {
            this.executeBean = entity.executeBean;
        }
        if (this.executeMethod == null) {
            this.executeMethod = entity.executeMethod;
        }
        return this;
    }
}
