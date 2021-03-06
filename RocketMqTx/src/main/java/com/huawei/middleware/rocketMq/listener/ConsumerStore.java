package com.huawei.middleware.rocketMq.listener;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ConsumerStore {

    public static final ConsumerStore INST = new ConsumerStore();

    private final List<String> consumerEntity = new ArrayList<>();

    private ConsumerStore() {
    }

    public boolean alreadyConsume(String uuid) {
        if (StringUtils.isEmpty(uuid)) {
            return true;
        }
        return consumerEntity.contains(uuid);
    }

    public void add(String uuid) {
        consumerEntity.add(uuid);
    }
}
