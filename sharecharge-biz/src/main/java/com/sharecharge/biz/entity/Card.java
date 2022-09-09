package com.sharecharge.biz.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.sharecharge.core.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * 充电卡
 */
@Data
@TableName("t_card")
public class Card {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer adminId;
    /**
     * 卡用戶名
     */
    @Excel(name = "用户名")
    private String userName;
    /**
     * 充电卡编号
     */
    @Excel(name = "卡号")
    private String cardNo;
    /**
     * 充电卡金额
     */
    @Excel(name = "卡余额")
    private Double cardCash;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 激活状态
     */
    private Integer activateStatus;
    /**
     * 充电卡状态
     */
    private Integer cardStatus;
    /**
     * 实际支付金額
     */
    @Excel(name = "实际支付金额")
    private Double realityPayMoney;
    /**
     * 平台金額
     */
    @Excel(name = "赠送金额")
    private Double giveMoney;
    /**
     * 每刷一次支付多少
     */
    @Excel(name = "每刷一次支付多少")
    private Double perPayMoney;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;
}
