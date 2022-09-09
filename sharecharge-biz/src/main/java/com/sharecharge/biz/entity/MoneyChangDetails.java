package com.sharecharge.biz.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 资金变动明细(t_money_chang_details)
 *
 * @author bianj
 * @version 1.0.0 2021-03-31
 */
@Data
@TableName("t_money_chang_details")
public class MoneyChangDetails {


    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 代理商ID
     */
    private Integer adminId;

    /**
     * 变动前金额
     */
    private Double beforeChangeCash;

    /**
     * 此次变动金额
     */
    private Double money;

    /**
     * 事件  例如 订单++++分润   或者 微信提现 或者订单----退款
     */
    private String event;

    /**
     * 1.订单分润  2 提现   3 退款
     */
    private Integer type;

    /**
     * 变动后金额
     */
    private Double afterChangeCash;

    /**
     * createTime
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
