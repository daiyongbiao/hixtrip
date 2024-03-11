package com.hixtrip.sample.app.service;

import com.hixtrip.sample.app.api.OrderService;
import com.hixtrip.sample.app.strategy.PayStatusHandler;
import com.hixtrip.sample.app.strategy.PayStatusStrategyService;
import com.hixtrip.sample.client.order.dto.CommandOderCreateDTO;
import com.hixtrip.sample.client.order.dto.CommandPayDTO;
import com.hixtrip.sample.client.sample.vo.OrderVO;
import com.hixtrip.sample.domain.commodity.CommodityDomainService;
import com.hixtrip.sample.domain.inventory.InventoryDomainService;
import com.hixtrip.sample.domain.order.OrderDomainService;
import com.hixtrip.sample.domain.order.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * app层负责处理request请求，调用领域服务
 */
@Component
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final InventoryDomainService inventoryDomainService;
    private final CommodityDomainService commodityDomainService;
    private final OrderDomainService orderDomainService;
    private final PayStatusStrategyService payStatusStrategyService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO placeOrder(CommandOderCreateDTO commandOderCreateDTO) {
        String orderId = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString();
        //简单加个锁, 当多线程的时候需要设置线程重试次数和线程休眠时间, 避免直接返回失败结果给用户
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock_place_order", uuid, 10, TimeUnit.SECONDS);
        Assert.isTrue(Boolean.FALSE.equals(lock), "订单火爆, 请重试");
        try {
            //查询suk库存
            Integer inventory = inventoryDomainService.getInventory(commandOderCreateDTO.getSkuId());
            Assert.isTrue(Objects.nonNull(inventory) && inventory >= commandOderCreateDTO.getAmount(), "商品库存不足");
            LocalDateTime now = LocalDateTime.now();
            Order order = Order.builder()
                    .id(orderId)
                    .userId(commandOderCreateDTO.getUserId())
                    .skuId(commandOderCreateDTO.getSkuId())
                    .amount(commandOderCreateDTO.getAmount())
                    .money(commodityDomainService.getSkuPrice(commandOderCreateDTO.getSkuId()))
                    .payTime(now)
                    .payStatus("await")
                    .createBy(commandOderCreateDTO.getUserId())
                    .createTime(now)
                    .build();
            orderDomainService.createOrder(order);
            //修改库存
            inventoryDomainService.changeInventory(commandOderCreateDTO.getSkuId(), -Long.valueOf(commandOderCreateDTO.getAmount()), Long.valueOf(commandOderCreateDTO.getAmount()), null);
        } finally {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "    return redis.call('del', KEYS[1]) " +
                    "else " +
                    "    return 0 " +
                    "end";
            redisTemplate.execute(
                    new DefaultRedisScript<Long>(script, Long.class),
                    Collections.singletonList("lock_place_order"),
                    uuid
            );
        }
        return OrderVO.of(orderId, commandOderCreateDTO.getSkuId(), "下单成功!");
    }

    @Override
    public String payCallback(CommandPayDTO commandPayDTO) {
        //查询缓存是否有回调记录
        String cacheKey = "repeat_callback:" + commandPayDTO.getOrderId();
        if (Objects.isNull(redisTemplate.opsForValue().get(cacheKey))) {
            PayStatusHandler payStatusHandler = payStatusStrategyService.handle(commandPayDTO.getPayStatus());
            payStatusHandler.handle(commandPayDTO);
            //回调记录保存一天,具体时长看需求
            redisTemplate.opsForValue().set(cacheKey, "1", Duration.ofDays(1));
        }
        return "success";
    }
}
