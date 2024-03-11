package com.hixtrip.sample.infra;

import com.hixtrip.sample.domain.inventory.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * infra层是domain定义的接口具体的实现
 */
@Component
public class InventoryRepositoryImpl implements InventoryRepository {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public Integer getInventory(String skuId) {
        //为了代码闭环这边缓存的操作给一个库存的初始值
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(skuId);
        if (entries.isEmpty()) {
            //给一个库存的初始值是100
            redisTemplate.opsForHash().putAll(skuId, Map.of(
                    "sellableQuantity", 100L,
                    "withholdingQuantity", 0L,
                    "occupiedQuantity", 0L
            ));
            return 100;
        }
        return (Integer) entries.get("sellableQuantity");
    }

    @Override
    public Boolean changeInventory(String skuId, Long sellableQuantity, Long withholdingQuantity, Long occupiedQuantity) {
        //为了代码闭环这边操作缓存里库存的加减
        HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
        Map<Object, Object> entries = opsForHash.entries(skuId);
        if (Objects.nonNull(sellableQuantity)) {
            opsForHash.put(skuId, "sellableQuantity", Long.parseLong(String.valueOf(entries.get("sellableQuantity"))) + sellableQuantity);
        }
        opsForHash.put(skuId, "withholdingQuantity", Long.parseLong(String.valueOf(entries.get("withholdingQuantity"))) + withholdingQuantity);
        if (Objects.nonNull(occupiedQuantity)) {
            opsForHash.put(skuId, "occupiedQuantity", Long.parseLong(String.valueOf(entries.get("occupiedQuantity"))) + occupiedQuantity);
        }
        return true;
    }
}
