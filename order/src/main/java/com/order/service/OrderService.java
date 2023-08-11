package com.order.service;

import com.order.entity.OrderEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ti
 * @since 2023-05-17
 */
public interface OrderService extends IService<OrderEntity> {


    Long createOrder(String orderNo);

    OrderEntity getOrderById(Long orderId);

}
