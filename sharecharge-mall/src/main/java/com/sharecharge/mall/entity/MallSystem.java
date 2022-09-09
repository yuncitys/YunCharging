package com.sharecharge.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("mall_system")
public class MallSystem {
    @TableId(type = IdType.AUTO)
    //("编号")
    private Integer id;
    //("系统配置名")
    private String keyName;
    //("系统配置值")
    private String keyValue;
    //("创建时间")
    private Date addTime;
    //("更新时间")
    private Date updateTime;
    //("逻辑删除")
    private Boolean deleted;
}
