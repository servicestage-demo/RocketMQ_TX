package com.huawei.middleware.rocketMq.client;

import com.huawei.middleware.rocketMq.annotations.RocketMqTxProducer;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class BankAImpl {

    @RocketMqTxProducer(identifier = "bankAB", txGroup = "mq_producer", topicCommit = "topic_commit", checkMethod = "checkMethod")
    @Transactional
    public void bankATransfer() {
        System.out.println("A 银行执行本地事务");
    }

    public void execute() {
        System.out.println("B 银行执行本地事务");
    }

    public boolean checkMethod() {
        System.out.println("A 银行检查事务结果");
        return true;
    }
}
