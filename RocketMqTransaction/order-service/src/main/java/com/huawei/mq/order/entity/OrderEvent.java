package com.huawei.mq.order.entity;

import lombok.Getter;

@Getter
public class OrderEvent {
    private String orderId;

    private String pId;

    private int num;

    public OrderEvent(String orderId, String pId, int num) {
        this.orderId = orderId;
        this.pId = pId;
        this.num = num;
    }

}
