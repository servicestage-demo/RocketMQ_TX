package com.huawei.mq.order.service;

import com.huawei.middleware.rocketMqSaga.annotations.RocketMqTxSaga;
import com.huawei.mq.order.entity.OrderEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

public class OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    protected JdbcTemplate jdbcTemplate;

    protected DataSource dataSource;

    public static final String CREATE_TABLE_SQL = "create table if not exists order_info\n" +
        "(\n" +
        "    orderId    varchar(128) not null,\n" +
        "    pId varchar(128) not null,\n" +
        "    num int null\n" +
        ");";

    public static final String INSERT_SQL = "insert into order_info(orderId,pId,num) VALUES (?, ?, ?);";

    public static final String TRUNCATE_SQL = "truncate table order_info;";

    public static final String QUERY_SQL = "select 1 from order_info where orderId = '%s' LIMIT 1;";

    public static final String REMOVE_SQL = "delete from order_info where orderId = '%s';";

    public OrderService(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    public void initOrder() {
        jdbcTemplate.execute(CREATE_TABLE_SQL);
        jdbcTemplate.execute(TRUNCATE_SQL);
    }

    @Transactional
    @RocketMqTxSaga(identifier = "orderSUC", txGroup = "mq_pro", topicCommit = "topic_commit",
        topicRollback = "topic_rollback", checkMethod = "checkOrder", cancelMethod = "cancelOrder")
    public void insertOrder(OrderEvent orderEvent) {
        jdbcTemplate.update(INSERT_SQL, orderEvent.getOrderId(), orderEvent.getPId(), orderEvent.getNum());
        LOGGER.info("订单系统 插入订单-{} , 货物-{}", orderEvent.getOrderId(), orderEvent.getPId());
    }

    @Transactional
    public boolean checkOrder(OrderEvent orderEvent) {
        SqlRowSet exist = jdbcTemplate.queryForRowSet(String.format(QUERY_SQL, orderEvent.getOrderId()));
        LOGGER.info("订单系统 检验事务结果");
        return exist.next();
    }

    @Transactional
    public void cancelOrder(OrderEvent orderEvent) {
        jdbcTemplate.update(String.format(REMOVE_SQL, orderEvent.getOrderId()));
        LOGGER.info("订单系统 回滚订单-{}", orderEvent.getOrderId());
    }

}
