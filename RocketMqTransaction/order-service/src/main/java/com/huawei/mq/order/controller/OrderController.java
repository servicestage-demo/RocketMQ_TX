package com.huawei.mq.order.controller;

import com.huawei.mq.order.entity.OrderEvent;
import com.huawei.mq.order.service.OrderService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.cloud.context.config.annotation.RefreshScope;

@RestController
@RefreshScope
@RequestMapping(value = "order-ser")
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    private static final String ORDER_FORMAT = "US_%s";

    @Autowired
    private OrderService orderService;

    /**
     * bankA 初始化
     */
    @GetMapping(value = "init")
    public String initOrder() {
        LOGGER.info("Order init");
        orderService.initOrder();
        return "ok";
    }

    /**
     * 购买成功
     */
    @GetMapping(value = "purchase")
    public String purchaseOrder(@RequestParam(value = "pId") String pId) {
        LOGGER.info("Order purchase");
        String orderId = String.format(ORDER_FORMAT, System.currentTimeMillis());
        orderService.insertOrder(new OrderEvent(orderId, pId, 1));
        return "ok";
    }

}
