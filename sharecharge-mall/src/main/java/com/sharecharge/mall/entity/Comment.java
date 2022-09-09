package com.sharecharge.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 评论
 */
@TableName("comment")
@Data
public class Comment {
    @TableId(type = IdType.AUTO)
   //("评论编号")
    private Integer id;
   //("如果type=0，则是商品评论；如果是type=1，则是专题评论。")
    private Integer valueId;
   //("评论类型，如果type=0，则是商品评论；如果是type=1，则是专题评论；")
    private Byte type;
   //("评论内容")
    private String content;
   //("商家回复内容")
    private String adminContent;
   //("用户表的用户ID")
    private Integer userId;
   //("是否含有图片")
    private Boolean hasPicture;
   //("图片地址列表，采用JSON数组格式")
    private String[] picUrls;
   //("评分， 1-5")
    private Short star;
   //("创建时间")
    private Date addTime;
   //("更新时间")
    private Date updateTime;
   //("逻辑删除")
    private Boolean deleted;
}
