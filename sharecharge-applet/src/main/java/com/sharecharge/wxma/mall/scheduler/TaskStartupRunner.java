package com.sharecharge.wxma.mall.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sharecharge.core.util.DateUtils;
import com.sharecharge.mall.config.MallSystemConfig;
import com.sharecharge.mall.entity.Order;
import com.sharecharge.mall.service.OrderService;
import com.sharecharge.mall.util.OrderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskStartupRunner implements ApplicationRunner {

    final OrderService orderService;
    final TaskService taskService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //List<Order> orderList = orderService.queryUnpaid(SystemConfig.getOrderUnpaid());
        log.info("-------查询过期订单------");
//        List<Order> orderList = orderService.list(new QueryWrapper<Order>().lambda().eq(Order::getOrderStatus, OrderUtil.STATUS_CREATE).eq(Order::getDeleted, false));
//        for (Order order : orderList) {
//            LocalDateTime add = DateUtils.dateToLocalDateTime(order.getAddTime());
//            LocalDateTime now = DateUtils.dateToLocalDateTime(new Date());
//            LocalDateTime expire = add.plusMinutes(MallSystemConfig.getOrderUnpaid());
//            if (expire.isBefore(now)) {
//                // 已经过期，则加入延迟队列
//                taskService.addTask(new OrderUnpaidTask(order.getId(), 0));
//            } else {
//                // 还没过期，则加入延迟队列
//                long delay = ChronoUnit.MILLIS.between(now, expire);
//                taskService.addTask(new OrderUnpaidTask(order.getId(), delay));
//            }
//        }
    }
}
