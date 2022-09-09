package com.sharecharge.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.mall.dto.QueryRequest;
import com.sharecharge.mall.entity.Order;
import com.sharecharge.mall.vo.OrderVo;

import javax.validation.constraints.NotNull;

public interface OrderService extends IService<Order> {

    ResultUtil submit(String body);

    int updateWithOptimisticLocker(Order order);

    void releaseCoupon(Integer orderId);

    ResultUtil list(OrderVo order, QueryRequest queryRequest);

    ResultUtil list(Integer userId, Integer showType, Integer pageSize, Integer pageNum);

    ResultUtil refund(Integer orderId, String refundMoney);

    ResultUtil ship(Integer orderId, String shipSn, String shipChannel);

    ResultUtil detail(Integer userId, @NotNull Integer orderId);

    ResultUtil cancel(Integer userId, @NotNull Integer orderId);


    ResultUtil receipt(Integer userId, @NotNull Integer orderId);

    boolean payOk(Integer userId, @NotNull Integer orderId, String out_trade_no);


    ResultUtil delete(Integer userId, @NotNull Integer orderId);

    ResultUtil orderInfo(Integer userId);

}
