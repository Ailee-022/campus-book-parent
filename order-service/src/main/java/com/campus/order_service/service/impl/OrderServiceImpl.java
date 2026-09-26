package com.campus.order_service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.order_service.entity.Order;
import com.campus.order_service.mapper.OrderMapper;
import com.campus.order_service.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
}