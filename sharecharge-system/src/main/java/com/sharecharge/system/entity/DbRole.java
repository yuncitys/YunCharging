package com.sharecharge.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
public class DbRole {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 角色名称
     */
    private String roleName;
    /**
     * 角色备注
     */
    private String remark;
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
     * 是否删除 0 未删除 1表示已删除
     */
    @TableLogic
    private Integer deleteStatus;
}
