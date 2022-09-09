package com.sharecharge.biz.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 设备故障记录
 */
@Data
@TableName("t_device_feeback")
public class DeviceFeeBack {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String deviceCode;
    private String reservedPhone;
    private String port;
    private String feeBackContent;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableLogic
    private Integer isDelete;
}
