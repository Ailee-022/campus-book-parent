package com.campus.order_service.controller;

import com.campus.common.BaseContext;
import com.campus.common.Result;
import com.campus.order_service.entity.Order;
import com.campus.order_service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired private OrderService orderService;
    @Autowired private RestTemplate restTemplate;

    // 创建订单
    @PostMapping("/create")
    public Result<String> create(@RequestParam Long bookId,
                                 @RequestParam Integer quantity,
                                 @RequestParam Integer version) {

        // 1. 远程调用 8082 服务，进行乐观锁扣减库存
        String url = "http://localhost:8082/book/deduct?bookId=" + bookId
                + "&quantity=" + quantity + "&version=" + version;

        Result<?> deductResult = restTemplate.postForObject(url, null, Result.class);

        if (deductResult == null || deductResult.getCode() != 200) {
            return Result.error("下单失败：" + (deductResult == null ? "服务异常" : deductResult.getMsg()));
        }

        // 2. 库存扣减成功后，生成订单
        Order order = new Order();
        order.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
        // 从 ThreadLocal 里拿当前登录用户的真实 ID
        order.setUserId(BaseContext.getCurrentId());  // 暂时写死，后续从 Token 里拿
        order.setTotalAmount(new BigDecimal(quantity * 45.50));
        order.setStatus(0); // 0 待支付

        orderService.save(order);
        return Result.success("下单成功！订单号：" + order.getOrderNo());
    }

    // 查询订单
    @GetMapping("/list")
    public Result<java.util.List<Order>> list() {
        return Result.success(orderService.list());
    }
}