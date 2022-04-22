package com.huawei.mq.invoke;

import com.huawei.mq.invoke.service.InvokeService;
import com.huawei.mq.invoke.utils.CmdUtils;
import com.huawei.mq.invoke.utils.MenuOpEnum;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 程序的入口，根据输入分别去调用不同的场景用例
 */
@Component
public class InvokeStarter implements ApplicationRunner {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(InvokeStarter.class);

    public static final String COMPUTER = "001";

    public static final String PHONE = "002";

    @Autowired
    private InvokeService invokeService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Thread.sleep(8000);
        while (true) {
            try {
                Arrays.stream(MenuOpEnum.values()).forEach(CmdUtils::println);
                CmdUtils.println("请输入命令执行操作：");
                int cmd = CmdUtils.readCmd(8);
                switch (MenuOpEnum.values()[cmd]) {
                    case EXIT:
                        System.exit(0);
                        break;
                    case INIT_DB:
                        invokeService.initOrder();
                        invokeService.initInventory();
                        break;
                    case ORDER_SUC:
                        invokeService.orderPur(PHONE);
                        break;
                    case ORDER_ERR:
                        invokeService.orderPur(COMPUTER);
                        break;
                }
            } catch (Throwable throwable) {
                // ignore
                LOGGER.error(throwable.getMessage());
            }
        }
    }
}
