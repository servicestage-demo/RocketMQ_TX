package com.huawei.middleware.rocketMqSaga.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RocketMqTxConsumer {
    /**
     * 唯一标识。
     *
     * @return txId
     */
    String identifier();

    /**
     * group
     *
     * @return txGroup
     */
    String txGroup();

    /**
     * topicCommit
     *
     * @return topicCommit
     */
    String topicCommit();

    /**
     * topicRollback
     *
     * @return topicRollback
     */
    String topicRollback();
}
