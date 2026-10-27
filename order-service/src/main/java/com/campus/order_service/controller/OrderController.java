package com.campus.order_service.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.common.BaseContext;
import com.campus.common.Result;
import com.campus.order_service.entity.Order;
import com.campus.order_service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/order")
@CrossOrigin("*")
public class OrderController {

    @Autowired private OrderService orderService;
    @Autowired private RestTemplate restTemplate;

    // 1. 单本下单
    @PostMapping("/create")
    public Result<String> create(@RequestParam Long bookId, @RequestParam Integer quantity, @RequestParam Integer version) {
        String url = "http://localhost:8082/book/deduct?bookId=" + bookId + "&quantity=" + quantity + "&version=" + version;
        Result<?> deductResult = restTemplate.postForObject(url, null, Result.class);
        if (deductResult == null || deductResult.getCode() != 200) {
            return Result.error("下单失败：" + (deductResult == null ? "服务异常" : deductResult.getMsg()));
        }
        Order order = new Order();
        order.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
        order.setUserId(BaseContext.getCurrentId());
        order.setTotalAmount(new BigDecimal(quantity * 45.50));
        order.setStatus(0);
        orderService.save(order);
        return Result.success("下单成功！订单号：" + order.getOrderNo());
    }

    // 2. 查全部订单
    @GetMapping("/list")
    public Result<List<Order>> list() {
        return Result.success(orderService.list());
    }

    // 3. 查我的订单
    @GetMapping("/my")
    public Result<List<Order>> getMyOrders() {
        Long userId = BaseContext.getCurrentId();
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("create_time");
        return Result.success(orderService.list(wrapper));
    }

    // 4. 购物车结算（包含分布式事务的手动补偿机制）
    @PostMapping("/cart/checkout")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> checkout(@RequestBody List<Map<String, Object>> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) return Result.error("购物车为空");

        List<Map<String, Object>> deductedBooks = new ArrayList<>();

        try {
            for (Map<String, Object> item : cartItems) {
                Long bookId = Long.valueOf(item.get("bookId").toString());
                Integer quantity = Integer.valueOf(item.get("quantity").toString());
                Integer version = Integer.valueOf(item.get("version").toString());

                String url = "http://localhost:8082/book/deduct?bookId=" + bookId + "&quantity=" + quantity + "&version=" + version;
                Result<?> deductResult = restTemplate.postForObject(url, null, Result.class);

                if (deductResult == null || deductResult.getCode() != 200) {
                    throw new RuntimeException("图书ID:" + bookId + "库存不足");
                }
                deductedBooks.add(item); // 记录成功扣减的图书
            }
        } catch (Exception e) {
            // 🚨 核心补偿：如果有报错，把前面扣成功的库存全部加回来！
            for (Map<String, Object> item : deductedBooks) {
                Long bookId = Long.valueOf(item.get("bookId").toString());
                Integer quantity = Integer.valueOf(item.get("quantity").toString());
                String revertUrl = "http://localhost:8082/book/revert?bookId=" + bookId + "&quantity=" + quantity;
                restTemplate.postForObject(revertUrl, null, Result.class);
            }
            return Result.error("下单失败（库存已全部自动恢复）：" + e.getMessage());
        }

        Order order = new Order();
        order.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
        order.setUserId(BaseContext.getCurrentId());
        order.setTotalAmount(new BigDecimal(100.00));
        order.setStatus(0);
        orderService.save(order);
        return Result.success("购物车结算成功！订单号：" + order.getOrderNo());
    }
}