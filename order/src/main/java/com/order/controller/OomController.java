package com.order.controller;

import com.order.entity.OrderEntity;
import com.springboot.common.entity.Result;
import com.springboot.common.utils.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Tag(name = "模拟OOM")
@Slf4j
@RestController
@RequestMapping("/oom")
public class OomController {


    @Operation(summary = "大对象")
    @PostMapping("/big/object")
    public Result<Void> bigObject(@RequestParam Long count) {
        List<OrderEntity> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            users.add(new OrderEntity());
        }
        return ResultUtil.success();
    }

    @Operation(summary = "大数组")
    @PostMapping("/big/array")
    public Result<Void> bigArray(@RequestParam Long count) {
        //SpringFactoriesLoader
        Object[] objects = new Object[count.intValue()];
        return ResultUtil.success();
    }


    @SuppressWarnings("AlibabaAvoidManuallyCreateThread")
    @Operation(summary = "创建许多线程")
    @PostMapping("/thread")
    public Result<Void> thread(@RequestParam Long count) {
        for (int i = 0; i < count; i++) {
            new Thread(() -> {
                log.info("创建线程[{}]", Thread.currentThread().getName());
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {

                }
            }, "线程-" + i).start();
        }
        return ResultUtil.success();
    }


    private static final ThreadLocal<OrderEntity> local = new ThreadLocal<>();

    @SuppressWarnings("AlibabaAvoidManuallyCreateThread")
    @Operation(summary = "ThreadLocal")
    @PostMapping("/threadlocal")
    public Result<Void> threadLocal(@RequestParam Long count) {

        for (int i = 0; i < count; i++) {
            new Thread(() -> {
                local.set(new OrderEntity());
            }).start();
        }
        return ResultUtil.success();
    }


    private static final List<OrderEntity> list = new ArrayList<>();

    @Operation(summary = "静态变量")
    @PostMapping("/static")
    public Result<Void> staticValue(@RequestParam Long count) {
        //AbstractQueuedSynchronizer
        for (int i = 0; i < count; i++) {
            list.add(new OrderEntity());
        }
        return ResultUtil.success();
    }

}
