package com.order.controller;

import com.order.entity.OrderEntity;
import com.order.service.OrderService;
import com.springboot.client.UserClientService;
import com.springboot.common.entity.Result;
import com.springboot.common.utils.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ti
 * @since 2023-05-17
 */
@Slf4j
@Tag(name = "订单管理")
@RestController
@RequestMapping("/admin")
public class OrderController {

    @DubboReference
    private UserClientService userClientService;

    @Autowired
    private OrderService orderService;

    @Operation(summary = "测试dubbo")
    @PostMapping(value = "/rpc/test")
    public Result<String> testDubbo(@RequestParam Long userId) {
        //NettyClient
        String userName = userClientService.getUserName(userId);
        return ResultUtil.success(userName);
    }

    @Operation(summary = "创建订单")
    @GetMapping(value = "/order/create")
    public Result<Long> createOrder(@RequestParam String orderNo) {
        Long id = orderService.createOrder(orderNo);
        return ResultUtil.success(id);
    }

    @Operation(summary = "获取订单")
    @GetMapping(value = "/order/get/{id}")
    public Result<OrderEntity> getOrder(@PathVariable Long id) {
        OrderEntity orderById = orderService.getOrderById(id);
        return ResultUtil.success(orderById);
    }

}
