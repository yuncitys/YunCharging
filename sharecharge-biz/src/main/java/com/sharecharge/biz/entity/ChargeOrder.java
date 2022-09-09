package com.sharecharge.biz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单表(t_order_info)
 *
 * @author bianj
 * @version 1.0.0 2021-03-31
 */
@Data
@TableName("t_order_info")
public class ChargeOrder {

    /**
     * 订单号
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * orderCode
     */
    private String orderCode;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 设备id
     */
    private Integer deviceId;

    /**
     * 订单总金额
     */
    private BigDecimal totalPrice;

    /**
     * 订单总支付金额
     */
    private BigDecimal actualPrice;

    /**
     * 订单实际支付金额
     */
    private BigDecimal realityPayMoney;

    /**
     * 设备Code
     */
    private String deviceCode;

    /**
     * 端口号
     */
    private Integer devicePort;

    /**
     * 充电类型  0刷卡充电 1 扫码充电 2免费充电
     */
    private Integer orderType;

    /**
     * 订单状态 （0.进行中，1、已完成）
     */
    private Integer orderStatus;

    /**
     * 支付状态
     */
    private Integer payStatus;

    /**
     * 订单创建时间
     */
    private Date createTime;

    /**
     * 设备工作开始时间
     */
    private Date startTime;

    /**
     * 设备工作结束时间
     */
    private Date endTime;

    /**
     * 选择充电时长
     */
    private String hour;

    /**
     * 收费类型
     */
    private Integer priceType;

    /**
     * 平台利润
     */
    private Double adminProfit;

    /**
     * 一级代理利润
     */
    private Double firstAgentProfit;

    /**
     * 二级代理利润
     */
    private Double secondAgentProfit;

    /**
     * thirdAgentProfit
     */
    private Double thirdAgentProfit;

    /**
     * 是否加入余额了  0  未加  1 已加
     */
    private Integer proxyMoneyStatus;

    private String cardNo;

    /**
     * 是否续充 0 未续充  1 已续充
     */
    private Integer isContinued;

    /**
     * 实时功率
     */
    private String  power;

    /**
     * 总电量
     */
    private Double totalPower;

    /**
     * 是否删除  0 未删除 1删除
     */
    @TableLogic
    private Integer isDelete;
}
