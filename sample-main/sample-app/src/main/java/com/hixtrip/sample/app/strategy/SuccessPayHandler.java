package com.hixtrip.sample.app.strategy;

import com.hixtrip.sample.client.order.dto.CommandPayDTO;
import com.hixtrip.sample.domain.order.OrderDomainService;
import com.hixtrip.sample.domain.pay.model.CommandPay;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 策略成功支付实现
 *
 * @Author irving
 * @Date 2024-03-07 10:42:52
 */
@Component("success")
@RequiredArgsConstructor
public class SuccessPayHandler implements PayStatusHandler {

    private final OrderDomainService orderDomainService;

    @Override
    public void handle(CommandPayDTO commandPayDTO) {
        orderDomainService.orderPaySuccess(new CommandPay(commandPayDTO.getOrderId(), commandPayDTO.getPayStatus()));
    }

}
