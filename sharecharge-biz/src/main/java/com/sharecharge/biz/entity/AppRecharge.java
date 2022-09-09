package com.sharecharge.biz.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * t_app_recharge
 *
 * @author bianj
 * @version 1.0.0 2021-06-01
 */
@Data
@TableName("t_app_recharge")
public class AppRecharge {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * rechargeAmount
     */
    private BigDecimal rechargeAmount;

    /**
     * giftAmount
     */
    private BigDecimal giftAmount;

    /**
     * adminId
     */
    private Integer adminId;

    /**
     * createTime
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    private Integer type;

    @TableLogic
    private Integer isDelete;
}
