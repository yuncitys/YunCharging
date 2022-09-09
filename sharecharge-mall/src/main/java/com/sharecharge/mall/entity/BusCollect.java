package com.sharecharge.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@TableName("bus_collect")
@Data
public class BusCollect {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty("编号")
    private Integer id;
    @ApiModelProperty("用户id")
    private Integer userId;
    @ApiModelProperty("商家id")
    private Integer busId;
    @ApiModelProperty("类型 0")
    private Integer type;
    @ApiModelProperty("添加时间")
    private LocalDateTime addTime;
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
    @ApiModelProperty("逻辑删除")
    private Boolean deleted;

}

