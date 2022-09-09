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

/**
 * t_device_type
 *
 * @author bianj
 * @version 1.0.0 2021-03-31
 */
@Data
@TableName("t_device_type")
public class DeviceType {
    @TableId
    private Integer deviceTypeId;

    private String deviceTypeName;

    private Integer portCount;

    @TableLogic
    private Integer isDelete;
}
