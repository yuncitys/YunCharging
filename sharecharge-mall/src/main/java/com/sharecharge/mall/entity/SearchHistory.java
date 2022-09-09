package com.sharecharge.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 搜索历史
 */
@TableName("search_history")
@Data
public class SearchHistory {
    @TableId(type = IdType.AUTO)
    //("编号")
    private Integer id;
    //("用户ID")
    private Integer userId;
    //("搜索关键字")
    private String keyword;
    //("搜索来源 如 pc wx app")
    private String source;
    //("创建时间")
    private Date addTime;
    //("更新时间")
    private Date updateTime;
    //("逻辑删除")
    private Boolean deleted;

}
