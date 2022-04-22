package com.huawei.middleware.rocketMqSaga;

import com.huawei.middleware.rocketMqSaga.client.BankAImpl;
import com.huawei.middleware.rocketMqSaga.client.OrderEntity;
import com.huawei.middleware.rocketMqSaga.client.bankBImpl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoSaga {

    @Resource
    BankAImpl bankA;

    @Resource
    bankBImpl bankB;

    @Test
    public void test() {
        OrderEntity order = new OrderEntity("US001123", "SAS_001");

        // bankA.bankATransfer(order);
        while (true) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
