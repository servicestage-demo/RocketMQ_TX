package com.huawei.middleware.rocketMqSaga.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RocketMqTxSaga {
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

    /**
     * 本地事务执行结果校验
     *
     * @return checkMethod
     */
    String checkMethod();

    /**
     * 本地事务回滚方法
     *
     * @return checkMethod
     */
    String cancelMethod();

}
