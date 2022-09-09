package com.sharecharge.biz.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * t_app_pay_details
 *
 * @author bianj
 * @version 1.0.0 2021-06-01
 */
@Data
@TableName("t_app_pay_details")
public class AppPayDetails {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 支付编码
     */
    private String payCode;

    /**
     * 支付金额
     */
    private BigDecimal payMoeny;

    /**
     * 赠送金额
     */
    private BigDecimal giftMoney;

    /**
     * 0未支付  1  已支付
     */
    private Integer payStatus;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 充值卡号
     */
    private String cardNo;
    /**
     * 充值类型 0单次充值 1充值套餐 2充值ic卡
     */
    private Integer type;
}
