package com.sharecharge.biz.entity;/*
 * Welcome to use the TableGo Tools.
 *
 * http://www.tablego.cn
 *
 * http://vipbooks.iteye.com
 * http://blog.csdn.net/vipbooks
 * http://www.cnblogs.com/vipbooks
 *
 * Author: bianj
 * Email: tablego@qq.com
 * Version: 6.6.6
 */

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 提现记录表(t_withdraw_cash_record)
 *
 * @author bianj
 * @version 1.0.0 2021-03-31
 */
@Data
@TableName("t_withdraw_cash_record")
public class WithdrawCashRecord {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 提现流水号
     */
    private String withdrawCode;

    /**
     * 申请人Id
     */
    private Integer adminId;//withdrawAdminId;

    /**
     * 提现金额
     */
    private BigDecimal money;

    /**
     * 提现时间
     */
    private Date createTime;

    /**
     * 付款人
     */
    private Integer payAdminId;

    /**
     * 付款时间
     */
    private Date payTime;

    /**
     * 提现状态: 0 提现中  ,1 提现成功  2 撤回 , 3退回
     */
    private Integer status;

    /**
     * 备注说明
     */
    private String remarks;

    /**
     * 删除: 0 : 未删除  1 已删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 1-微信零钱
     */
    private Integer payType;
}
