package com.huawei.mq.invoke.utils;

public enum MenuOpEnum {
    INIT_DB("初始化数据库"),
    ORDER_SUC("购买商品手机（有库存）"),
    ORDER_ERR("购买商品电脑（无库存）"),
    EXIT("EXIT");

    private String des;

    MenuOpEnum(String des) {
        this.des = des;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s;", ordinal(), this.des);
    }

    public String getDes() {
        return des;
    }
}
