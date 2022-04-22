package com.huawei.middleware.rocketMq.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RocketMqTxProducer {

    /**
     * 唯一标识。
     *
     * @return identifier
     */
    String identifier();

    /**
     * group
     *
     * @return txGroup
     */
    String txGroup();

    /**
     * topic
     *
     * @return topic
     */
    String topicCommit();

    /**
     * 本地事务执行结果校验
     *
     * @return checkMethod
     */
    String checkMethod();

}
