package com.hixtrip.sample.app.strategy;

import com.hixtrip.sample.client.order.dto.CommandPayDTO;
import com.hixtrip.sample.domain.order.OrderDomainService;
import com.hixtrip.sample.domain.pay.model.CommandPay;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 策略失败支付实现
 *
 * @Author irving
 * @Date 2024-03-07 10:44:27
 */
@Component("fail")
@RequiredArgsConstructor
public class FailPayHandler implements PayStatusHandler {

    private final OrderDomainService orderDomainService;

    @Override
    public void handle(CommandPayDTO commandPayDTO) {
        orderDomainService.orderPayFail(new CommandPay(commandPayDTO.getOrderId(), commandPayDTO.getPayStatus()));
    }
}
