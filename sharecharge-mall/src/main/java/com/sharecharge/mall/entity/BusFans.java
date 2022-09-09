package com.sharecharge.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商家粉丝
 */
@TableName("bus_fans")
@Data
public class BusFans {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer busId;
    private Integer totalFans;
    private LocalDateTime addTime;
    private LocalDateTime updateTime;
    private Integer deleted;

}
