package com.huawei.middleware.rocketMq;

import com.huawei.middleware.rocketMq.client.BankAImpl;
import com.huawei.middleware.rocketMq.client.bankBImpl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Demo {

    @Resource
    BankAImpl bankA;

    @Resource
    bankBImpl bankB;

    @Test
    public void test() {
        bankA.bankATransfer();
        while (true) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

}
