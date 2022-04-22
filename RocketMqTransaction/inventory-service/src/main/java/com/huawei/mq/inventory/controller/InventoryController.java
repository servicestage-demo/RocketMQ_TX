package com.huawei.mq.inventory.controller;

import com.huawei.mq.inventory.service.InventoryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
@RequestMapping(value = "inventory-ser")
public class InventoryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryController.class);

    @Autowired
    private InventoryService inventoryService;

    /**
     * Inventory 初始化
     */
    @GetMapping(value = "init")
    @Transactional
    public String initInventory() {
        LOGGER.info("Inventory init");
        inventoryService.initInventory();
        return "ok";
    }

    /**
     * 减库存 成功
     */
    @Transactional
    @GetMapping(value = "inventorySUC")
    public String inventorySUC() {
        LOGGER.info("consume inventory");
        return "ok";
    }

}
