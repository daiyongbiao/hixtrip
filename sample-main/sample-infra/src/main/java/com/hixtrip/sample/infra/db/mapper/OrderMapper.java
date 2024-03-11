package com.hixtrip.sample.infra.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hixtrip.sample.infra.db.dataobject.OrderDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

/**
 * @Author irving
 * @Date 2024-03-07 17:04:20
 */
public interface OrderMapper extends BaseMapper<OrderDO> {

    // 使用注解方式定义更新操作
    @Update("UPDATE order_0 SET pay_status = #{payStatus}, update_time = #{now} WHERE id = #{id}")
    int updatePayStatusById(@Param("id") String id, @Param("payStatus") String payStatus, @Param("update_time") LocalDateTime now);

}
