package com.sharecharge.mall.vo;

import lombok.Data;

@Data
public class OrderVo {
    //("用户表的用户昵称")
    private String userName;
    //("订单编号")
    private String orderSn;
    //("订单状态")
    private String[] orderStatus;
    //("收货人名称")
    private String consignee;
    //("开始时间")
    private String startTime;
    //("结束时间")
    private String overTime;

}
