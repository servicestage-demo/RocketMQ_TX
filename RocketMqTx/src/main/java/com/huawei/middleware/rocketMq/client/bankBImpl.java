package com.huawei.middleware.rocketMq.client;

import com.huawei.middleware.rocketMq.annotations.RocketMqTxConsumer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class bankBImpl {

    @RocketMqTxConsumer(identifier = "bankAB", txGroup = "mq_consumer", topicCommit = "topic_commit")
    @Transactional
    public void bankBTransfer() {
        System.out.println("B客户执行本地事务");
    }

}
