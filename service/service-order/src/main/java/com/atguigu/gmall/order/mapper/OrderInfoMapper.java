package com.atguigu.gmall.order.mapper;

import com.atguigu.gmall.model.order.OrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Connor
 * @description 针对表【order_info(订单表 订单表)】的数据库操作Mapper
 * @createDate 2022-09-13 09:52:43
 * @Entity com.atguigu.gmall.order.domain.OrderInfo
 */
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    /**
     * 修改订单状态
     *
     * @param orderId 订单ID
     * @param userId 用户ID
     * @param orderStatus 订单状态
     * @param processStatus 处理状态
     * @param expects 当前期待状态
     */
    void updateOrderStatus(@Param("orderId") Long orderId, @Param("userId") Long userId, @Param("orderStatus") String orderStatus, @Param("processStatus") String processStatus, @Param("expects") List<String> expects);
}




