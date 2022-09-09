package com.sharecharge.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
public class DbMenu {
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 父节点
     */
    private Integer parentId;
    /**
     * 菜单名
     */
    private String title;
    /**
     * 图标
     */
    private String icon;
    /**
     * 资源地址
     */
    private String href;
    /**
     * 权限
     */
    private String perms;
    /**
     * true：展开，false：不展开
     */
    private String spread;
    /**
     * 排序
     */
    private Integer sorting;
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
    private Integer deleteStatus;

}
