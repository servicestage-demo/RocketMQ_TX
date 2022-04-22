package com.huawei.middleware.rocketMqSaga;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TxResultCache {
    private static final Map<String, Boolean> TxResult = new ConcurrentHashMap<>();

    public static void store(String txid, boolean result) {
        TxResult.put(txid, result);
    }

    public static boolean query(String txid) {
        if (TxResult.containsKey(txid)) {
            return TxResult.get(txid);
        } else {
            return false;
        }
    }
}
