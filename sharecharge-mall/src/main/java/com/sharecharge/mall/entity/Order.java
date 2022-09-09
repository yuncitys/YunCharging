package com.sharecharge.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@TableName("t_order")
public class Order {
    @TableId(type = IdType.AUTO)
    private Integer id;
   //("商家id")
    private Integer businessId;
   //("用户表的用户ID")
    private Integer userId;
   //("用户表的用户昵称")
    @TableField(exist = false)
    private String userName;
   //("订单编号")
    private String orderSn;
   //("订单状态")
    private Short orderStatus;
   //("售后状态，0是可申请，1是用户已申请，2是管理员审核通过，3是管理员退款成功，4是管理员审核拒绝，5是用户已取消")
    private Short aftersaleStatus;
   //("收货人名称")
    private String consignee;
   //("收货人手机号")
    private String mobile;
   //("收货具体地址")
    private String address;
   //("用户订单留言")
    private String message;
   //("商品总费用")
    private BigDecimal goodsPrice;
   //("配送费用")
    private BigDecimal freightPrice;
   //("优惠券减免")
    private BigDecimal couponPrice;
   //("用户积分减免")
    private BigDecimal integralPrice;
   //("团购优惠价减免")
    private BigDecimal grouponPrice;
   //("订单费用")
    private BigDecimal orderPrice;
   //("实付费用")
    private BigDecimal actualPrice;
   //("微信付款编号")
    private String payId;
   //("微信付款时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date payTime;
   //("发货编号")
    private String shipSn;
   //("发货快递公司")
    private String shipChannel;
   //("发货开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date shipTime;
   //("实际退款金额")
    private BigDecimal refundAmount;
   //("退款方式")
    private String refundType;
   //("退款备注")
    private String refundContent;
   //("退款时间")
    private Date refundTime;
   //("用户确认收货时间")
    private Date confirmTime;
   //("待评价订单商品数量")
    private Short comments;
   //("订单关闭时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
   //("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;
   //("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
   //("逻辑删除")
    private Boolean deleted;

    private transient List<OrderGoods> goodsList;
}
