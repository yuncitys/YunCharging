package com.sharecharge.web.mall;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.mall.dto.QueryRequest;
import com.sharecharge.mall.entity.Order;
import com.sharecharge.mall.entity.OrderGoods;
import com.sharecharge.mall.service.OrderGoodsService;
import com.sharecharge.mall.service.OrderService;
import com.sharecharge.mall.util.OrderUtil;
import com.sharecharge.mall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


@Controller
@RequestMapping("webOrder")
@ResponseBody
public class WebOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderGoodsService orderGoodsService;

    @GetMapping("/list")
    public ResultUtil list(OrderVo order, QueryRequest queryRequest) {
        return orderService.list(order, queryRequest);
    }

    /**
     * 删除
     *
     * @param ，{ orderId：xxx, shipSn: xxx, shipChannel: xxx }
     * @return 订单操作结果
     */
    @PostMapping("/delete")
    public ResultUtil delete(String ids) {
        String[] arr = ids.split(StringPool.COMMA);
        for (int i = 0; i < arr.length; i++) {
            Order order = orderService.getOne(new QueryWrapper<Order>().lambda().eq(Order::getId, arr[i]));
            if (order != null) {
                // 如果订单不是关闭状态(已取消、系统取消、已退款、用户已确认、系统已确认)，则不能删除
                Short status = order.getOrderStatus();
                if (!status.equals(OrderUtil.STATUS_CANCEL) && !status.equals(OrderUtil.STATUS_AUTO_CANCEL) &&
                        !status.equals(OrderUtil.STATUS_CONFIRM) && !status.equals(OrderUtil.STATUS_AUTO_CONFIRM) &&
                        !status.equals(OrderUtil.STATUS_REFUND_CONFIRM)) {
                    return ResultUtil.error("订单不能删除");
                }

                order.setDeleted(true);
                // 删除订单
                orderService.updateById(order);

                List<OrderGoods> orderGoods = orderGoodsService.list(new QueryWrapper<OrderGoods>().lambda().eq(OrderGoods::getOrderId, order.getId()));
                if (orderGoods != null && !orderGoods.isEmpty()) {
                    for (OrderGoods item : orderGoods) {
                        item.setDeleted(true);
                        // 删除订单商品
                        orderGoodsService.updateById(item);
                    }
                }

            }
        }
        return ResultUtil.success();
    }

    @GetMapping("/detail")
    public ResultUtil detail(@NotNull Integer id) {
        return ResultUtil.success(orderService.getById(id));

    }

    /**
     * 订单退款
     *
     * @param { orderId：xxx }
     * @return 订单退款操作结果
     */
    @PostMapping("/refund")
    public Object refund(@NotBlank Integer orderId, String refundMoney) {
        return orderService.refund(orderId, refundMoney);
    }

    /**
     * 发货
     *
     * @param ，{ orderId：xxx, shipSn: xxx, shipChannel: xxx }
     * @return 订单操作结果
     */
    @PostMapping("/ship")
    public Object ship(@NotBlank Integer orderId, String shipSn, String shipChannel) {
        return orderService.ship(orderId, shipSn, shipChannel);
    }
}
