package com.atguigu.gmall.order.service.impl;


import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
* @author Connor
* @description 针对表【order_info(订单表 订单表)】的数据库操作Service实现
* @createDate 2022-09-13 09:52:43
*/
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo>
    implements OrderInfoService {

}




