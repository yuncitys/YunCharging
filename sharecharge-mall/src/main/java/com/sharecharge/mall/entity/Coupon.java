package com.sharecharge.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sharecharge.mall.mybatis.JsonIntegerArrayTypeHandler;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 优惠卷
 */
@TableName(value = "coupon",autoResultMap = true)
@Data
public class Coupon {
    @TableId(type = IdType.AUTO)
    //("优惠券编号")
    private Integer id;
    //("商家编号")
    private Long busId;
    //("优惠券名称")
    private String name;
    //("优惠券介绍")
    private String cDesc;
    //("优惠券标签，例如新人专用")
    private String tag;
    //("优惠券数量，如果是0，则是无限量")
    private Integer total;
    //("优惠金额")
    private BigDecimal discount;
    //("最少消费金额才能使用优惠券。")
    private BigDecimal min;
    //("用户领券限制数量，如果是0，则是不限制；默认是1，限领一张")
    private Short cLimit;
    //("优惠券赠送类型，如果是0则通用券，用户领取；如果是1，则是注册赠券；如果是2，则是优惠券码兑换")
    private Short type;
    //("优惠券状态，如果是0则是正常可用；如果是1则是过期; 如果是2则是下架。")
    private Short status;
    //("商品限制类型，如果0则全商品，如果是1则是类目限制，如果是2则是商品限制。")
    private Short goodsType;
    //("商品限制值，goods_type如果是0则空集合，如果是1则是类目集合，如果是2则是商品集合。")
    @TableField(typeHandler= JsonIntegerArrayTypeHandler.class)
    private Integer[] goodsValue;
    //("优惠券兑换码")
    private String code;
    //("有效时间限制，如果是0，则基于领取时间的有效天数days；如果是1，则start_time和end_time是优惠券有效期；")
    private Short timeType;
    //("基于领取时间的有效天数days。")
    private Short days;
    //("使用券开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
    //("使用券截至时间")
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
}
