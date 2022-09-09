package com.sharecharge.system.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

@Data
public class DbLog {

    private static final long serialVersionUID = 1L;

    private Integer id;
    /**
     * 管理端用户名
     */
    private String adminName;
    /**
     * 操作
     */
    private String operation;
    /**
     * 执行方法
     */
    private String method;
    /**
     * 请求参数
     */
    private String params;
    /**
     * ip
     */
    private String ip;
    /**
     * 操作时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

}
