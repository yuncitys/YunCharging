package com.sharecharge.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * 商品规格表
 */
@Data
@TableName(value = "goods_specification")
public class GoodsSpecification implements Serializable {
    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @NotBlank(message = "{required}")
    //("商品表的商品ID")
    private Integer goodsId;

    @NotBlank(message = "{required}")
    //("商品规格名称")
    private String specification;

    @NotBlank(message = "{required}")
    //("商品规格值")
    private String value;

    //("商品规格图片")
    private String picUrl;

    //("创建时间")
    private Date addTime;

    //("更新时间")
    private Date updateTime;

    private Boolean deleted;
}
