package com.sharecharge.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 收藏表
 */
@TableName("collect")
@Data
public class Collect {
    @TableId(type = IdType.AUTO)
   //("收藏编号")
    private Integer id;
   //("用户表的用户ID")
    private Integer userId;
   //("商品id")
    private Integer valueId;
   //("收藏类型，如果type=0，则是商品ID；如果type=1，则是专题ID")
    private Byte type;
   //("创建时间")
    private Date addTime;
   //("更新时间")
    private Date updateTime;
   //("逻辑删除")
    private Boolean deleted;
}
