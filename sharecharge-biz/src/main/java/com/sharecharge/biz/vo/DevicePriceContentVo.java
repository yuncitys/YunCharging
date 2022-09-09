package com.sharecharge.biz.vo;

import com.sharecharge.biz.entity.PriceContent;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class DevicePriceContentVo {
    private Integer id;

    /**
     * 方案名称
     */
    private String feeName;

    /**
     * 方案类型0 时间  1电量 2功率
     */
    private Integer priceType;

    /**
     * 方案状态0 未启用 1已启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 添加方案创建人id
     */
    private Integer adminUserId;

    /**
     * 实时收费类型 0:实时1分钟收费 1:实时30分钟收费
     */
    private Integer realTimeCharging;

    List<PriceContent> priceContents;
}
