package com.sharecharge.biz.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 功率图(t_power_record)
 *
 * @author bianj
 * @version 1.0.0 2021-05-06
 */
@Data
@TableName("t_power_record")
public class PowerRecord {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * deviceCode
     */
    private String deviceCode;

    private String port;

    /**
     * power
     */
    private String power;

    /**
     * syTime
     */
    private String syTime;

    /**
     * creatTime
     */
    @TableField(fill = FieldFill.INSERT)
    private String createTime;
}
