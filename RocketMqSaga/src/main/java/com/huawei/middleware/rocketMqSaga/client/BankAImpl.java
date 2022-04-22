package com.huawei.middleware.rocketMqSaga.client;

import com.huawei.middleware.rocketMqSaga.annotations.RocketMqTxSaga;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankAImpl {

    // @RocketMqTxSaga(identifier = "bankABSaga", txGroup = "mq_producer", topicCommit = "topic_commit",
    //     topicRollback = "topic_rollback", checkMethod = "checkMethod", cancelMethod = "cancelMethod")
    // @Transactional
    // public void bankATransfer(OrderEntity order) {
    //     System.out.printf("订单 %s 执行本地事务  %n",order.getOrderId());
    // }
    //
    // public boolean checkMethod(OrderEntity order) {
    //     System.out.printf("订单 %s 检查本地事务  %n",order.getOrderId());
    //     return true;
    // }
    //
    // public void cancelMethod(OrderEntity order) {
    //     System.out.printf("订单 %s 回滚本地事务  %n",order.getOrderId());
    // }
}
