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

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 设备价格设置表(t_device_price)
 *
 * @author bianj
 * @version 1.0.0 2021-03-31
 */
@Data
@TableName("t_device_price")
public class DevicePrice {

    /**
     * id
     */
    @TableId(type = IdType.NONE)
    private Integer id;

    /**
     * 方案名称
     */
    private String feeName;

    /**
     * 方案类型0 时间  1电量 2功率
     */
    private Integer priceType;

    /**
     * 方案状态0 未启用 1已启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 添加方案创建人id
     */
    private Integer adminUserId;

    /**
     * 实时收费类型 0:实时1分钟收费 1:实时30分钟收费
     */
    private Integer realTimeCharging;

    /**
     * 是否删除 0 未删除  1 删除
     */
    @TableLogic
    private Integer isDelete;


    /**
     * 创建人
     */
    @TableField(exist = false)
    private String adminName;
    @TableField(exist = false)
    private String adminFullname;

    /**
     * 方案详情
     */
    @TableField(exist = false)
    List<PriceContent> priceContentList;

}
