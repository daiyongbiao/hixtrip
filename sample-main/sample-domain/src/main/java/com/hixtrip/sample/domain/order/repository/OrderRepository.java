package com.hixtrip.sample.domain.order.repository;

import com.hixtrip.sample.domain.order.model.Order;
import com.hixtrip.sample.domain.pay.model.CommandPay;

/**
 *
 */
public interface OrderRepository {

    /**
     * 创建订单
     * @param order
     */
    void createOrder(Order order);

    /**
     * 订单成功
     * @param commandPay
     */
    void orderPaySuccess(CommandPay commandPay);

    /**
     * 修改订单失败状态
     *
     * @param commandPay
     */
    void orderPayFail(CommandPay commandPay);

}
