package com.sharecharge.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 商品销量
 */
@TableName(value = "goods_sales",autoResultMap = true)
@Data
public class GoodsSales {
    @TableId(type = IdType.AUTO)
    private Integer id;
   //("商品编号")
    private Integer goodId;
   //("销量数量")
    private Integer salesSum;
   //("逻辑删除")
    private Boolean deleted;

}
