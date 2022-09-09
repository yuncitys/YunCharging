package com.sharecharge.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户浏览足迹表
 */
@TableName("footprint")
@Data
public class Footprint {
    @TableId(type = IdType.AUTO)
    //("足迹编号")
    private Integer id;
    //("用户编号")
    private Integer userId;
    //("商品编号")
    private Integer goodsId;
    //("创建时间")
    private Date addTime;
    //("更新时间")
    private Date updateTime;
    //("逻辑删除")
    private Boolean deleted;
}
