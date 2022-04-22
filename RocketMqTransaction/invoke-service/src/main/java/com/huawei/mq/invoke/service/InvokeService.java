package com.huawei.mq.invoke.service;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class InvokeService {

    private final RestTemplate restTemplate;

    private static final String ORDER_INIT = "http://ORDER/order-ser/init";

    private static final String INVENTORY_INIT = "http://INVENTORY/inventory-ser/init";

    private static final String ORDER_PUR = "http://ORDER/order-ser/purchase?pId=%s";

    public InvokeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 初始化数据库
     * 创建 order 表
     */
    public void initOrder() {
        restTemplate.getForObject(ORDER_INIT, String.class);
    }

    /**
     * 初始化数据库
     * 创建 Inventory 表
     */
    public void initInventory() {
        restTemplate.getForObject(INVENTORY_INIT, String.class);
    }

    public void orderPur(String pId) {
        restTemplate.getForObject(String.format(ORDER_PUR, pId), String.class);
    }

}
