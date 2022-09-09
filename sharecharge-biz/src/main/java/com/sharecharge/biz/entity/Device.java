package com.sharecharge.biz.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 设备表(t_device)
 *
 * @author bianj
 * @version 1.0.0 2021-03-31
 */
@Data
@TableName("t_device")
public class Device{

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 设备号
     */
    private String deviceCode;

    /**
     * 设备imei号码
     */
    private String deviceImei;

    /**
     * 网点ID
     */
    private Integer networkDotId;

    /**
     * 设备类型ID
     */
    private String deviceTypeId;

    /**
     * 一级代理ID
     */
    private Integer firstAgentId;

    /**
     * 二级代理ID
     */
    private Integer secondAgentId;

    /**
     * 三级代理ID
     */
    private Integer thirdAgentId;

    /**
     * 软件版本号
     */
    private String deviceVersion;

    /**
     * 设备信号
     */
    private String deviceSignal;

    /**
     * 设备机箱温度
     */
    private String deviceTemperature;

    /**
     * 设备实施总功率
     */
    private String deviceTotalPower;

    /**
     * 设备电量
     */
    private Double deviceElectricity;

    /**
     * 设备在线离线状态 0 离线  1 在线
     */
    private Integer deviceStatus;

    /**
     * 设备心跳时间
     */
    private Integer deviceHeartbeatTime;

    /**
     * 收费类型 0 时间 1电量 2 免费
     */
    private Integer deviceChargePattern;

    /**
     * 等待时间
     */
    private Integer waitTime;

    /**
     * 功率下限
     */
    private Integer powerLower;

    /**
     * 功率上线
     */
    private Integer powerUpper;

    /**
     * 总功率上限
     */
    private Integer totalPowerUpper;

    /**
     * 低温度
     */
    private Double lowTemperature;

    /**
     * 高温度
     */
    private Double highTemperature;

    /**
     * 警告温度
     */
    private Double warningTemperature;

    /**
     * 端口状态 0 空闲  1充电中
     */
    private Integer port1;

    /**
     * 端口2状态 0 空闲  1充电中
     */
    private Integer port2;

    /**
     * 端口3状态 0 空闲  1充电中
     */
    private Integer port3;

    /**
     * 端口3状态 0 空闲  1充电中
     */
    private Integer port4;

    /**
     * 端口状态 0 空闲  1充电中
     */
    private Integer port5;

    /**
     * 端口状态 0 空闲  1充电中
     */
    private Integer port6;

    /**
     * 端口状态 0 空闲  1充电中
     */
    private Integer port7;

    /**
     * 端口状态 0 空闲  1充电中
     */
    private Integer port8;

    /**
     * 端口状态 0 空闲  1充电中
     */
    private Integer port9;

    /**
     * 端口状态 0 空闲  1充电中
     */
    private Integer port10;

    /**
     * 激活状态 0 未激活  1已激活
     */
    private Integer activateStatus;

    /**
     * 激活时间
     */
    private Date activateTime;

    /**
     * 收费方案id
     */
    private Integer devicePriceId;

    /**
     * 端口总数
     */
    private Integer portCount;

    /**
     * 端口状态 0 空闲  1充电中
     */
    private Integer port11;

    /**
     * 端口2状态 0 空闲  1充电中
     */
    private Integer port12;

    /**
     * 端口3状态 0 空闲  1充电中
     */
    private Integer port13;

    /**
     * 端口3状态 0 空闲  1充电中
     */
    private Integer port14;

    /**
     * 端口状态 0 空闲  1充电中
     */
    private Integer port15;

    /**
     * 端口状态 0 空闲  1充电中
     */
    private Integer port16;

    /**
     * 端口状态 0 空闲  1充电中
     */
    private Integer port17;

    /**
     * 端口状态 0 空闲  1充电中
     */
    private Integer port18;

    /**
     * 端口状态 0 空闲  1充电中
     */
    private Integer port19;

    /**
     * 端口状态 0 空闲  1充电中
     */
    private Integer port20;

    /**
     * 设备操作状态 0正常 1禁用
     */
    private Integer operationState;

    /**
     * 分配状态
     */
    private Integer  allocationStatus;

    /**
     * 设备精度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String  latitude;

    /**
     * 是否删除  0 未删除  1删除
     */
    @TableLogic
    private Integer isDelete;


//    /**
//     * 方案啊类型 0 时间  1电量 2 按功率段收费
//     */
//    @TableField(exist = false)
//    private Integer priceType;
//
//    /**
//     * 实时收费类型 0:实时1分钟收费 1:实时30分钟收费
//     */
//    @TableField(exist = false)
//    private Integer realTimeCharging;
//
//    @TableField(exist = false)
//    List<PriceContent> priceContentList;

}
