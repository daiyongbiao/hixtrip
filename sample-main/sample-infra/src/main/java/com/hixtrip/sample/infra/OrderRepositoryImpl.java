package com.hixtrip.sample.infra;

import com.hixtrip.sample.domain.inventory.InventoryDomainService;
import com.hixtrip.sample.domain.order.model.Order;
import com.hixtrip.sample.domain.order.repository.OrderRepository;
import com.hixtrip.sample.domain.pay.model.CommandPay;
import com.hixtrip.sample.infra.db.convertor.OrderDOConvertor;
import com.hixtrip.sample.infra.db.dataobject.OrderDO;
import com.hixtrip.sample.infra.db.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @Author irving
 * @Date 2024-03-07 12:10:02
 */
@Component
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderMapper orderMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private final InventoryDomainService inventoryDomainService;

    @Override
    public void createOrder(Order order) {
        OrderDO orderDO = OrderDOConvertor.INSTANCE.domainToDo(order);
        orderMapper.insert(orderDO);
    }

    @Override
    public void orderPaySuccess(CommandPay commandPay) {
        orderMapper.updatePayStatusById(commandPay.getOrderId(), commandPay.getPayStatus(), LocalDateTime.now());
        OrderDO orderDO = orderMapper.selectById(commandPay.getOrderId());
        inventoryDomainService.changeInventory(orderDO.getSkuId(), null, -Long.valueOf(orderDO.getAmount()), Long.valueOf(orderDO.getAmount()));
    }

    @Override
    public void orderPayFail(CommandPay commandPay) {
        orderMapper.updatePayStatusById(commandPay.getOrderId(), commandPay.getPayStatus(), LocalDateTime.now());
        OrderDO orderDO = orderMapper.selectById(commandPay.getOrderId());
        inventoryDomainService.changeInventory(orderDO.getSkuId(), Long.valueOf(orderDO.getAmount()), -Long.valueOf(orderDO.getAmount()), null);
    }
}
