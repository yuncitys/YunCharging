package com.sharecharge.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sharecharge.mall.mybatis.JsonStringArrayTypeHandler;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * 购物车商品表
 */
@TableName(value = "cart",autoResultMap = true)
@Data
public class Cart implements Serializable {
    private static final long serialVersionUID=1L;
    @TableId(type = IdType.AUTO)
    //("编号")
    private Integer id;

    //("用户表的用户ID")
    private Integer userId;

    @NotNull(message = "{required}")
    //("商品表的商品ID")
    private Integer goodsId;

    //("商品编号")
    private String goodsSn;

    //("商家id")
    private Integer businessId;

    //("商品名称")
    private String goodsName;

    //("商品货品表的货品ID")
    @NotNull(message = "{required}")
    private Integer productId;

    //("商品货品的价格")
    private BigDecimal price;

    //("商品货品的数量")
    @NotNull(message = "{required}")
    private Integer number;

    //("商品规格值列表，采用JSON数组格式")
    @TableField(typeHandler= JsonStringArrayTypeHandler.class)
    private String[] specifications;

    //("购物车中商品是否选择状态")
    private Boolean checked;

    //("商品图片或者商品货品图片")
    private String picUrl;

    //("创建时间")
    private LocalDateTime addTime;

    //("更新时间")
    private LocalDateTime updateTime;

    //("逻辑删除")
    private Boolean deleted;


}
