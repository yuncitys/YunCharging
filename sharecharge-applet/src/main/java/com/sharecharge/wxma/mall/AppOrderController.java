package com.sharecharge.wxma.mall;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.mall.entity.Order;
import com.sharecharge.mall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Controller
@RequestMapping("/app/order")
@ResponseBody
public class AppOrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    public ResultUtil submit(@RequestBody String body) {
        return orderService.submit(body);
    }

    @GetMapping("/list")
    public ResultUtil list(
            Integer userId,
            @RequestParam(defaultValue = "0") Integer showType,
            @RequestParam(defaultValue = "1") Integer pageSize,
            @RequestParam(defaultValue = "10") Integer pageNum) {
        return orderService.list(userId, showType, pageSize, pageNum);

    }

    @GetMapping("/detail")
    public ResultUtil detail(Integer userId, @NotNull Integer orderId) {
        return orderService.detail(userId, orderId);
    }

    @GetMapping("/cancel")
    public ResultUtil cancel(Integer userId, @NotNull Integer orderId) {
        return orderService.cancel(userId, orderId);
    }

    @DeleteMapping
    public ResultUtil delete(Integer userId, @NotNull Integer orderId) {
        return orderService.delete(userId, orderId);
    }


    @PutMapping("update")
    public ResultUtil update(@NotBlank String id, @NotBlank Integer type) {
        Order serviceOne = orderService.getOne(new QueryWrapper<Order>().lambda().eq(Order::getId, id));
        if (serviceOne != null) {

            switch (type) {
                case 1:
                    if (serviceOne.getOrderStatus() != 101) {
                        return ResultUtil.error("订单不能被取消！");
                    }
                    serviceOne.setOrderStatus(new Short("102"));
                    break;
                case 2:
                    if (serviceOne.getOrderStatus() != 201) {
                        return ResultUtil.error("订单不能退款！");
                    }
                    serviceOne.setOrderStatus(new Short("202"));
                    break;
            }
            orderService.updateById(serviceOne);
        }
        return ResultUtil.success();
    }

    @GetMapping("/receipt")
    public ResultUtil receipt(Integer userId, @NotNull Integer orderId) {
        return orderService.receipt(userId, orderId);
    }

}
