package com.hixtrip.sample.app.strategy;

import com.hixtrip.sample.client.order.dto.CommandPayDTO;

/**
 * 策略支付状态接口
 *
 * @Author irving
 * @Date 2024-03-07 10:40:12
 */
public interface PayStatusHandler {

    void handle(CommandPayDTO commandPayDTO);

}
