package com.sharecharge.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;


/**
 * 商品参数表
 */
@TableName("goods_attribute")
@Data
public class GoodsAttribute implements Serializable {
    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
   //("编号")
    private Integer id;

    @NotBlank(message = "{required}")
   //("商品表的商品ID")
    private Integer goodsId;

    @NotBlank(message = "{required}")
   //("商品参数名称")
    private String attribute;

    @NotBlank(message = "{required}")
   //("商品参数值")
    private String value;

   //("创建时间")
    private Date addTime;

   //("更新时间")
    private Date updateTime;

    //逻辑删除
    private Boolean deleted;


}
