package com.sharecharge.biz.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 设备管理需要显示的设备信息
 */
@Data
public class DeviceVo {
    /**id*/
    private Integer id;
    /**设备号*/
    private String deviceCode;
    /**设备imei*/
    private String deviceImei;
    /**设备状态设备在线离线状态 0 离线  1 在线',*/
    private Integer deviceStatus;
    /**设备激活时间 */
    private Date activateTime;
    /**设备激活状态*/
    private Integer activateStatus;
    /**设备创建时间*/
    private Date createTime;
    /**设备类型名字*/
    private String deviceTypeName;
    /**网点名称*/
    private String networkName;
    /**网点地址*/
    private String networkAddress;
    /**设备版本*/
    private String deviceVersion;
    /**设备信号强度*/
    private String deviceSignal;

    /**一级代理商名称*/
    private String firstAdminName;
    private String firstAdminFullname;

    /**二级代理商名称*/
    private String secondAdminName;
    private String secondAdminFullname;

    /**三级代理商名称*/
    private String threeAdminName;
    private String threeAdminFullname;

    /**分配状态*/
    private Integer allocationStatus;

    /**
     * 收费类型
     */
    private Integer deviceChargePattern;
    /**
     * 方案id
     */
    private Integer devicePriceId;
    /**
     * 网点id
     */
    private Integer networkDotId;
    /**
     * 一级代理
     */
    private Integer firstAgentId;
    /**
     * 二级代理
     */
    private Integer secondAgentId;
    /**
     * 三级代理
     */
    private Integer thirdAgentId;

    /**
     * 端口总数
     */
    private Integer portCount;

    /**
     * 操作状态
     */
    private Integer operationState;

    /**
     * 设备经度纬度
     */
    private String longitude;

    /**
     * 设备经度纬度
     */
    private String latitude;

    /**方案名称*/
    private String feeName;

}
