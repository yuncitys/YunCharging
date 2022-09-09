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

/**
 * 充电桩--网点(t_network_dot)
 *
 * @author bianj
 * @version 1.0.0 2021-03-31
 */
@Data
@TableName("t_network_dot")
public class NetworkDot{

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 网点名称
     */
    private String networkName;
    /**
     * 网点省份
     */
    private String networkProvince;
    /**
     * 网点位置
     */
    private String networkAddress;

    /**
     * 网点经度
     */
    private String networkLongitude;

    /**
     * 网点纬度
     */
    private String networkLatitude;

    /**
     * 可用数量
     */
    private Integer usableNum;

    /**
     * 代理商ID
     */
    private Integer adminId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;


    /**
     * 表中无字段  展示效果   距离
     */
    @TableField(exist = false)
    private Double distance;
}
