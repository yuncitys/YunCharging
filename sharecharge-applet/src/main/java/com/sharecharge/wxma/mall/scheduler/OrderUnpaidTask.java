package com.sharecharge.wxma.mall.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sharecharge.core.util.spring.SpringUtils;
import com.sharecharge.mall.config.MallSystemConfig;
import com.sharecharge.mall.entity.Order;
import com.sharecharge.mall.entity.OrderGoods;
import com.sharecharge.mall.service.GoodsProductService;
import com.sharecharge.mall.service.OrderGoodsService;
import com.sharecharge.mall.service.OrderService;
import com.sharecharge.mall.util.OrderUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.List;

public class OrderUnpaidTask extends Task {
    private final Log logger = LogFactory.getLog(OrderUnpaidTask.class);

    private int orderId = -1;

    public OrderUnpaidTask(Integer orderId, long delayInMilliseconds) {
        super("OrderUnpaidTask-" + orderId, delayInMilliseconds);
        this.orderId = orderId;
    }

    public OrderUnpaidTask(Integer orderId) {
        super("OrderUnpaidTask-" + orderId, MallSystemConfig.getOrderUnpaid() * 60 * 1000);
        this.orderId = orderId;
    }

    @Override
    public void run() {
        logger.info("系统开始处理延时任务---订单超时未付款---" + this.orderId);

        OrderService orderService = SpringUtils.getBean(OrderService.class);
        OrderGoodsService orderGoodsService = SpringUtils.getBean(OrderGoodsService.class);
        GoodsProductService productService = SpringUtils.getBean(GoodsProductService.class);
        OrderService wxOrderService = SpringUtils.getBean(OrderService.class);

        Order order = orderService.getById(this.orderId);
        if (order == null) {
            return;
        }
        if (!OrderUtil.isCreateStatus(order)) {
            return;
        }

        // 设置订单已取消状态
        order.setOrderStatus(OrderUtil.STATUS_AUTO_CANCEL);
        order.setEndTime(new Date());
        if (orderService.updateWithOptimisticLocker(order) == 0) {
            throw new RuntimeException("更新数据已失效");
        }

        // 商品货品数量增加
        Integer orderId = order.getId();
        List<OrderGoods> orderGoodsList = orderGoodsService.list(new QueryWrapper<OrderGoods>().lambda().eq(OrderGoods::getOrderId, orderId).eq(OrderGoods::getDeleted, false));
        for (OrderGoods orderGoods : orderGoodsList) {
            Integer productId = orderGoods.getProductId();
            Short number = orderGoods.getNumber();
            if (productService.addStock(productId, number) == 0) {
                throw new RuntimeException("商品货品库存增加失败");
            }
        }
        //返还优惠券
        wxOrderService.releaseCoupon(orderId);

        logger.info("系统结束处理延时任务---订单超时未付款---" + this.orderId);
    }
}
