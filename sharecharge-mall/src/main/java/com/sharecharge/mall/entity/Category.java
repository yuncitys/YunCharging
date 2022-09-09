package com.sharecharge.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 *  类目实体类
 */
@TableName("category")
@Data
public class Category {

    @TableId(type = IdType.AUTO)
   //("类目编号")
    private Integer id;
    @NotBlank(message = "{required}")
   //("类目名称")
    private String name;
   //("类目关键字，以JSON数组格式")
    private String keywords;
   //("类目广告语介绍")
    private String descInfo;//cDesc desc
   //("父类目ID")
    @NotNull(message = "{required}")
    private Integer pid;
   //("类目图标")
    private String iconUrl;
   //("类目图片")
    private String picUrl;
   //("等级")
    @NotBlank(message = "{required}")
    private String level;
   //("排序")
    private Byte sortOrder;
   //("创建时间")
    private Date addTime;
   //("更新时间")
    private Date updateTime;
   //("逻辑删除")
    private Boolean deleted;

    private transient List<Category> categoryChildren;
}
