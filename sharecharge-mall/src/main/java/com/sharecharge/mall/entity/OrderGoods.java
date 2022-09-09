package com.sharecharge.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sharecharge.mall.mybatis.JsonStringArrayTypeHandler;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单商品表
 */
@Data
@TableName(value = "order_goods",autoResultMap = true)
public class OrderGoods {
    @TableId(type = IdType.AUTO)
   //("订单商品编号")
    private Integer id;
   //("订单表的订单ID")
    private Integer orderId;
   //("商品表的商品ID")
    private Integer goodsId;
   //("商品名称")
    private String goodsName;
   //("商品编号")
    private String goodsSn;
   //("商品货品表的货品ID")
    private Integer productId;
   //("商品货品的购买数量")
    private Short number;
   //("商品货品的售价")
    private BigDecimal price;
   //("商品货品的规格列表")
    @TableField(typeHandler= JsonStringArrayTypeHandler.class)
    private String[] specifications;
   //("商品货品图片或者商品图片")
    private String picUrl;
   //("订单商品评论，如果是-1，则超期不能评价；如果是0，则可以评价；如果其他值，则是comment表里面的评论ID。")
    private Integer comment;
   //("创建时间")
    private Date addTime;
   //("更新时间")
    private Date updateTime;
   //("逻辑删除")
    private Boolean deleted;
}
