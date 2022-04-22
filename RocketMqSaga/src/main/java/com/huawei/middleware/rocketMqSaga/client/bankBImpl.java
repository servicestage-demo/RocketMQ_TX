package com.huawei.middleware.rocketMqSaga.client;

import com.huawei.middleware.rocketMqSaga.annotations.RocketMqTxConsumer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class bankBImpl {

    // @RocketMqTxConsumer(identifier = "bankABSaga", txGroup = "mq_consumer", topicCommit = "topic_commit", topicRollback = "topic_rollback")
    // @Transactional
    // public void bankBTransfer(OrderEntity order) {
    //     // System.out.printf("物流 %s 执行本地事务成功  %n",order.getOrderId());
    //
    //     System.out.printf("物流 %s 执行本地事务失败  %n",order.getOrderId());
    //     throw new RuntimeException();
    // }

}
