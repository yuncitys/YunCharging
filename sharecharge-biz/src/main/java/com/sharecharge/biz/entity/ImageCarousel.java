package com.sharecharge.biz.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 图标轮播(t_image_carousel)
 *
 * @author bianj
 * @version 1.0.0 2021-03-31
 */
@Data
@TableName("t_image_carousel")
public class ImageCarousel{

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 图片标题
     */
    private String imageTitle;

    /**
     * 图片地址
     */
    private String imageUrl;

    /**
     * 图片跳转链接
     */
    private String imageLink;

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
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


    private Integer types;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleteStatus;
}
