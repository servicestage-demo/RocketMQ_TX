package com.huawei.mq.inventory.service;

import com.huawei.middleware.rocketMqSaga.annotations.RocketMqTxConsumer;
import com.huawei.mq.inventory.entity.OrderEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

public class InventoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryService.class);

    protected JdbcTemplate jdbcTemplate;

    protected DataSource dataSource;

    public static final String CREATE_TABLE_SQL = "create table if not exists inventory_info\n" +
        "(\n" +
        "    pId varchar(128) not null primary key,\n" +
        "    product varchar(128) not null,\n" +
        "    num int null\n" +
        ");";

    public static final String TRUNCATE_SQL = "truncate table inventory_info;";

    public static final String INSERT_SQL = "insert ignore into inventory_info VALUES (?, ?, ?);";

    public static final String GET_SQL = "select num from inventory_info where pId = '%s';";

    public static final String CON_SQL = "update inventory_info set num = '%s' where pId = '%s';";

    public InventoryService(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    public void initInventory() {
        jdbcTemplate.execute(CREATE_TABLE_SQL);
        jdbcTemplate.execute(TRUNCATE_SQL);
        jdbcTemplate.update(INSERT_SQL, "001", "computer", 0);
        jdbcTemplate.update(INSERT_SQL, "002", "phone", 1000);
    }

    @Transactional
    @RocketMqTxConsumer(identifier = "orderSUC", txGroup = "mq_con", topicCommit = "topic_commit",
        topicRollback = "topic_rollback")
    public void consumeProduct(OrderEvent orderEvent) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(String.format(GET_SQL, orderEvent.getPId()));
        int num = 0;
        if (rowSet.next()) {
            num = rowSet.getInt("num");
        }
        if (num >= orderEvent.getNum()) {
            jdbcTemplate.update(String.format(CON_SQL, num - orderEvent.getNum(), orderEvent.getPId()));
            LOGGER.info("仓储 减少物品-{} , 出货-{}", orderEvent.getPId(), orderEvent.getNum());
        } else {
            LOGGER.info("仓储 物品-{}  库存不足", orderEvent.getPId());
            throw new RuntimeException();
        }
    }

}
