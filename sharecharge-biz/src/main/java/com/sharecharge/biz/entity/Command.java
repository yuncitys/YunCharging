package com.sharecharge.biz.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 设备指令表(t_command)
 *
 * @author bianj
 * @version 1.0.0 2021-03-31
 */
@Data
@TableName("t_command")
public class Command  extends Model<Command> {
    /**
     * 指令ID
     */
    @TableId(type = IdType.AUTO)
    private Integer commandId;

    /**
     * 指令名称
     */
    private String commandName;

    /**
     * 指令
     */
    private String command;

    /**
     * 指令描述
     */
    private String commandDesc;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 1：有效；0：无效
     */
    @TableLogic
    private Integer isDelete;
}
