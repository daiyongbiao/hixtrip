package com.hixtrip.sample.app.strategy;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 支付状态策略组
 *
 * @Author irving
 * @Date 2024-03-07 10:20:16
 */
@Component
public class PayStatusStrategyService {

    private final Map<String, PayStatusHandler> callbackMap = new ConcurrentHashMap<>();

    public PayStatusStrategyService(Map<String, PayStatusHandler> callbackMap) {
        this.callbackMap.putAll(callbackMap);
    }

    public PayStatusHandler handle(String payStatus) {
        PayStatusHandler callback = callbackMap.get(payStatus);
        if (callback == null) {
            throw new IllegalStateException("未找到对应的支付状态回调");
        }
        return callback;
    }

}
