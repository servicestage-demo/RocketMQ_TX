# RocketMqSaga
提供了两个注解
- @RocketMqTxSaga(identifier = "", txGroup = "", topicCommit = "",topicRollback = "", checkMethod = "", cancelMethod = "")
注解在produce的事务方法上

identifier: 唯一标识 

txGroup: 生产者的topic设置

topicCommit: 正向提交时, producer发送消息、consumer监听消息的topic

topicRollback: 出现异常情况下, 需要执行回滚方法, producer发送消息、consumer监听消息的topic

checkMethod: 校验生产者方法是否执行成功

cancelMethod: 出现异常情况下，执行的回滚方法

- @RocketMqTxConsumer(identifier = "", txGroup = "", topicCommit = "", topicRollback = "")
注解在consumer的事务方法上

identifier: 唯一标识

txGroup: 消费者的topic设置

topicCommit: 正向提交时, producer发送消息、consumer监听消息的topic

topicRollback: 出现异常情况下, 需要执行回滚方法, producer发送消息、consumer监听消息的topic

# RocketMqTransaction
使用样例
![捕获](https://user-images.githubusercontent.com/31872042/164741786-e9ba10a9-b8e6-4169-b44d-e54726731855.PNG)




# RocketMqTx
为 RocketMqSaga 的第一代版本  仅实现正向
- @RocketMqTxSaga(identifier = "", txGroup = "", topicCommit = "",topicRollback = "", checkMethod = "", cancelMethod = "")



- @RocketMqTxConsumer(identifier = "", txGroup = "", topicCommit = "", topicRollback = "")
