package com.sharecharge.biz.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 方案详情
 */
@Data
@TableName("t_price_content")
public class PriceContent {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 方案类型id
     */
    private Integer devicePriceId;
    /**
     * 方案时长
     */
    private String duration;
    /**
     * 方案金额
     */
    private Double money;

    /**
     * 功率段最小值
     */
    private String powerSectionBefore;

    /**
     * 功率最大值
     **/
    private String powerSectionAfter;

}
