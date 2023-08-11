package com.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.order.entity.OrderEntity;
import com.order.mapper.OrderMapper;
import com.order.service.OrderService;
import com.springboot.client.UserClientService;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ti
 * @since 2023-05-17
 */
@Slf4j
//@DubboService
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderEntity> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @DubboReference(init = false)
    private UserClientService userClientService;

   private AtomicInteger atomicInteger=new AtomicInteger(0);

    //@GlobalTransactional(timeoutMills = 600000000)
    //@Transactional(rollbackFor = Exception.class)
    @Override
    public Long createOrder(String orderNo) {
        log.info("全局事务ID:[{}]" , RootContext.getXID());

        //创建用户
        //Long userId = userClientService.addUser("测试分布式事务用户-"+atomicInteger.getAndIncrement());
        //插入订单
        OrderEntity order = new OrderEntity();
        order.setOrderNo(orderNo);
        //order.setUserId(userId);
        order.setUserId(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
        order.setProductId(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
        order.setCount(ThreadLocalRandom.current().nextInt(100000));
        orderMapper.insert(order);
       // int a=1/0;
        return order.getId();
    }

    @Override
    public OrderEntity getOrderById(Long orderId) {
        return orderMapper.selectById(orderId);
    }

}
