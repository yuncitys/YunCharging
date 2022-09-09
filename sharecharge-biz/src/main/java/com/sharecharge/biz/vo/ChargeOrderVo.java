package com.sharecharge.biz.vo;

import com.sharecharge.biz.entity.DevicePrice;
import lombok.Data;

import java.util.Date;

@Data
public class ChargeOrderVo {
    //订单Id
    private Integer id;
    //订单号
    private String orderCode;
    //微信OPenid
    private String wxOpenId;
    //用户余额
    private double cash;
    //用户ID
    private Integer userId;
    //设备ID
    private Integer deviceId;
    //设备号
    private String deviceCode;
    //订单总金额
    private double totalPrice;
    //实付支付总价（赠送加实际）
    private double actualPrice;
    //实际支付金额
    private double realityPayMoney;
    //设备端口号
    private Integer devicePort;
    //订单类型
    private Integer orderType;
    //订单状态
    private Integer orderStatus;
    //支付状态
    private Integer payStatus;
    //网点名称
    private String networkName;
    //网点地址
    private String networkAddress;
    //卡号
    private String cardNo;
    //充电时间
    private double hour;
    //创建时间
    private Date createTime;
    //开始时间
    private Date startTime;
    //结束时间
    private Date endTime;
    //收费类型
    private Integer priceType;
    //是否续充
    private Integer isContinued;
    //实时功率
    private String power;

    DevicePrice devicePrice;
}
