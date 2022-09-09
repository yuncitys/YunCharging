package com.sharecharge.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sharecharge.mall.mybatis.JsonStringArrayTypeHandler;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品货品表
 */
@TableName(value = "goods_product",autoResultMap = true)
@Data
public class GoodsProduct  implements Serializable {
    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    //("编号")
    private Integer id;

    @NotBlank(message = "{required}")
    //("商品表的商品ID")
    private Integer goodsId;

    //("商品规格值列表，采用JSON数组格式")
    @TableField(typeHandler= JsonStringArrayTypeHandler.class)
    private String[] specifications;

    //("商品货品价格")
    private BigDecimal price;

    //("商品货品数量")
    private Integer number;

    //("商品货品图片")
    private String url;

    //("创建时间")
    private Date addTime;

    //("更新时间")
    private Date updateTime;

    private Boolean deleted;
}
