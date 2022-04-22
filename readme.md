# RocketMqSaga
提供了两个注解
@RocketMqTxSaga(identifier = "", txGroup = "", topicCommit = "",topicRollback = "", checkMethod = "", cancelMethod = "")
@RocketMqTxConsumer(identifier = "", txGroup = "", topicCommit = "", topicRollback = "")

# RocketMqTransaction
使用样例

# RocketMqTx
为 RocketMqSaga 的第一代版本  仅实现正向
@RocketMqTxSaga(identifier = "", txGroup = "", topicCommit = "",topicRollback = "", checkMethod = "", cancelMethod = "")
@RocketMqTxConsumer(identifier = "", txGroup = "", topicCommit = "", topicRollback = "")
